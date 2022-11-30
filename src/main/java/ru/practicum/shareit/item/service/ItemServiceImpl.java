package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepositoryJpa;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.itemDto.LastBooking;
import ru.practicum.shareit.item.itemDto.NextBooking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentRepositoryJpa commentRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        checkUser(ownerId);
        User owner = UserMapper.toUser(userService.getById(ownerId));
        owner.setId(ownerId);
        itemDto.setOwner(owner);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
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
            if (dto.getRequest() != null) {
                item.setRequest(dto.getRequest());
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
    public List<ItemDto> getAll(Long userId) {
        //Получить список бронирований по итему
            User user = UserMapper.toUser(userService.getById(userId));
            user.setId(userId);
            List<ItemDto> items = new ArrayList<>();
                for (Item item : itemRepository.findAllByOwner(user)) {
                    Long ownerId = item.getOwner().getId();
                if (ownerId.equals(userId)) {
                    ItemDto itemDto1 = ItemMapper.toItemDto(item);
                    itemDto1 = itemDtoBuild(itemDto1,item.getId(),userId);
                    items.add(itemDto1);
                }
            }
        return items;

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
            throw new WrongParameterException("Вы не пользовались вещью (наверно)");
        }
        User author = UserMapper.toUser(userService.getById(userId));
        author.setId(userId);
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(itemRepository.getById(itemId))
                .author(author)
                .created(LocalDateTime.now())
                .build();
        return toCommentDto(commentRepository.save(comment));
    }

    private CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .build();
    }

    private boolean commentCheck(Long itemId, Long authorId) {
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId and b.booker.id = :authorId and b.end < :now and b.status = :status");
        query.setParameter("itemId", itemId);
        query.setParameter("authorId", authorId);
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("status", Status.APPROVED);
        List<Booking> commentBookings = query.list();
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

    private ItemDto itemDtoBuild(ItemDto itemDto,Long id, Long userId) {
        //Получить
        try {
            itemDto.setLastBooking(getLastBooking(id, userId));
            itemDto.setNextBooking(getNextBooking(id, userId));
        } finally {
            return itemDto;
        }
    }
    private LastBooking getLastBooking(Long itemId, Long userId) {
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId order by b.id asc");
        query.setParameter("itemId", itemId);
        List<Booking> itemBookings = query.list();
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
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId and b.start > :now order by b.start asc");
        query.setParameter("itemId", itemId);
        query.setParameter("now", LocalDateTime.now());
        List<Booking> itemBookings = query.list();
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
}
