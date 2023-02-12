package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepositoryJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.itemDto.LastBooking;
import ru.practicum.shareit.item.itemDto.NextBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepositoryJpa commentRepository;

    private final BookingRepository bookingRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        checkUser(ownerId);
        User owner = UserMapper.toUser(userService.getById(ownerId));
        owner.setId(ownerId);
        itemDto.setOwner(new ItemDto.Owner(ownerId, owner.getName()));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item save = itemRepository.save(item);
        log.info("ItemsSave {} {}",save.getRequest(),save.toString());
        return ItemMapper.toItemDto(save);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto dto, Long ownerId, Long itemId) {
        User owner = UserMapper.toUser(userService.getById(ownerId));
        owner.setId(ownerId);
        checkUser(ownerId);
        Item item = itemRepository. findById(itemId).orElseThrow(() ->
                new WrongParameterException("Вещь не найдена"));
        Long itemOwnerId = item.getOwner().getId();

            if (!(itemOwnerId.equals(owner.getId()))) {
                throw new NotFoundException("Изменять может только владелец");
            }
            if (dto.getAvailable() != null) {
                item.setAvailable(dto.getAvailable());
            }
            if (dto.getName() != null && !(dto.getName().isBlank())) {
                item.setName(dto.getName());
            }
            if (dto.getDescription() != null && !(dto.getDescription().isBlank())) {
                item.setDescription(dto.getDescription());
            }
            if (dto.getOwner() != null) {
                item.setOwner(owner);
            }
            if (dto.getRequestId() != null) {
                item.setRequest(dto.getRequestId());
            }
        item.setId(itemId);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getByID(Long id, Long userId) {
        Item item;
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            item = optionalItem.get();
        } else {
            throw new NotFoundException("Нет вещи с id =" + id);
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(getCommentsByItem(item));
        try {
            itemDto.setLastBooking(getLastBooking(id, userId));
            itemDto.setNextBooking(getNextBooking(id, userId));
        } finally {
            return itemDto;
        }
    }

    @Override
    public List<ItemDto> getAllByOwnerId(Long userId) {
        User user = UserMapper.toUser(userService.getById(userId));
        user.setId(userId);
        List<Item> items = itemRepository.findAllByOwner(user);
            Map<Item, List<Booking>> approvedBookings =  bookingRepository.findApprovedForItems(items, sort)
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));
        System.out.println(approvedBookings.toString());
        LocalDateTime now = LocalDateTime.now();
        List<ItemDto> itemDtoList = new ArrayList<>();

        List<Comment> comments =
                commentRepository.findAllByItemIn(items, Sort.by(Sort.Direction.DESC, "created"))
                        .stream()
                        .collect(Collectors.toUnmodifiableList());groupingBy(Comment::getItem, toList());

        for (Item i : items) {
            ItemDto itemDto = ItemMapper.toItemDto(i);
            Booking nextBooking = approvedBookings.getOrDefault(i, Collections.emptyList()).stream()
                    .filter(booking -> (booking.getStart().isAfter(now)))
                    .reduce((first, second) -> second)
                    .orElse(null);

            if (nextBooking != null) {
                itemDto.setNextBooking(
                        new NextBooking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }


            Booking lastBooking = approvedBookings.getOrDefault(i, Collections.emptyList()).stream()
                    .filter(b -> ((b.getEnd().isEqual(now) || b.getEnd().isBefore(now))
                        || (b.getStart().isEqual(now) || b.getStart().isBefore(now))))
                .findFirst()
                .orElse(null);
            if (lastBooking != null) {
                itemDto.setLastBooking(new LastBooking(lastBooking.getId(), lastBooking.getBooker().getId()));

            }

            List<Comment> addComment = comments.stream()
                    .filter(comment -> (itemDto.getId().equals(comment.getItem().getId())))
                    .collect(Collectors.toList());
            itemDto.setComments(addComment.stream().map(this::toCommentDto).collect(Collectors.toList()));
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }


    @Override
    public List<ItemDto> search(String request) {
        ArrayList<ItemDto> items = new ArrayList<>();
        if (!request.isBlank()) {
            for (Item item : itemRepository.findAll()) {
                if (item.getAvailable() &&
                        item.getDescription().toLowerCase().contains(request.toLowerCase()) ||
                        item.getName().toLowerCase().contains(request.toLowerCase())) {
                    items.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return items;
    }

    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        if (!commentCheck(itemId,userId)) {
            throw new WrongParameterException("Вы не пользовались вещью");
        }
        return toCommentDto(commentRepository.save(
                Comment.builder()
                .text(commentDto.getText())
                .item(itemRepository.getById(itemId))
                .author(UserMapper.toUser(userService.getById(userId)))
                .created(LocalDateTime.now())
                .build()));
    }

    @Override
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .build();
    }

    private boolean commentCheck(Long itemId, Long authorId) {
        List<Booking> commentBookings = itemRepository.commentCheck(itemId, authorId, LocalDateTime.now(), Status.APPROVED);
        if (commentBookings.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private List<CommentDto> getCommentsByItem(Item item) {
        List<Comment> comments = new ArrayList<>();
        comments.addAll(commentRepository.findAllByItem(item));
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment c: comments) {
            commentDtoList.add(toCommentDto(c));
        }
        return commentDtoList;
    }

    private LastBooking getLastBooking(Long itemId, Long userId) {
        List<Booking> itemBookings = itemRepository.getItemBookings(itemId);
        Booking booking = new Booking();
        for (Booking b: itemBookings) {
            Long ownerId = b.getItem().getOwner().getId();
            if (
                    ownerId.equals(userId)
            ) {
                booking = b;
                break;
            }
        }
        LastBooking lastBooking = new LastBooking();
        lastBooking.setId(booking.getId());
        lastBooking.setBookerId(booking.getBooker().getId());
        return lastBooking;
    }

    private NextBooking getNextBooking(Long itemId, Long userId) {
        List<Booking> itemBookings = itemRepository.getItemNextBooking(itemId,LocalDateTime.now());
        Booking booking = new Booking();
        for (Booking b: itemBookings) {
            Long ownerId = b.getItem().getOwner().getId();
            if (ownerId.equals(userId)) {
                booking = b;
                break;
            }
        }
        NextBooking nextBooking = new NextBooking();
        nextBooking.setId(booking.getId());
        nextBooking.setBookerId(booking.getBooker().getId());
        return nextBooking;
    }

    private void checkUser(Long userId) {
        try {
            userService.getById(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Юзер с ID " + userId + " не найден " + e.getLocalizedMessage());
        }
    }

    @Override
    public List<Item> getItemsByRequest(Long requestId) {
        return itemRepository.getByRequestOrderById(requestId);
    }
}
