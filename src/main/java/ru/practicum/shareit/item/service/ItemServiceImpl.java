package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
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
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final CommentRepositoryJpa commentRepository;
    private final EntityManager entityManager;
    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkUser(userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepositoryJpa.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto dto, Long userId, Long itemId) {
        checkUser(userId);
        Item item = ItemMapper.toItem(getByID(itemId, userId));
        if (item != null) {
            if (!(item.getOwner().equals(userId))) {
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
                item.setOwner(dto.getOwner());
            }
            if (dto.getRequest() != null) {
                item.setRequest(dto.getRequest());
            }

            } else {
                throw  new WrongParameterException("Вещь не найдена");
           }
        item.setId(itemId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepositoryJpa.save(item));
    }

    @Override
    public ItemDto getByID(Long id, Long userId) {
        Item item;
        Optional<Item> optionalItem = itemRepositoryJpa.findById(id);
        if(optionalItem.isPresent()){
            item = optionalItem.get();
        }
        else {
            throw new NotFoundException("Нет вещи с id =" + id);
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);
    //    List<Comment> comments = commentRepository.findAllByItemContaining(id);
        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList = getCommentsByItem(id);
//        for (Comment c:comments) {
//            commentDtoList.add(toCommentDto(c));
//        }

        itemDto.setComments(commentDtoList);
        try {
            itemDto.setLastBooking(getLastBooking(id, userId));
            itemDto.setNextBooking(getNextBooking(id, userId));
        }finally {
            return itemDto;
        }
    }

    private ItemDto itemDtoBuild(ItemDto itemDto,Long id, Long userId){
        try {
            itemDto.setLastBooking(getLastBooking(id, userId));
            itemDto.setNextBooking(getNextBooking(id, userId));
        }finally {
            return itemDto;
        }
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
            List<ItemDto> items = new ArrayList<>();
            for (Item item : itemRepositoryJpa.findAll()) {
                if (item.getOwner().equals(userId)) {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    items.add(itemDtoBuild(itemDto,item.getId(),userId));
                }
            }
        return items;
    }

    @Override
    public List<ItemDto> search(String request) {
        ArrayList<ItemDto> items = new ArrayList<>();
        if (!request.isBlank()) {
            for (Item item :itemRepositoryJpa.findAll()) {
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
        if (!commentCheck(itemId,userId)){
            throw new WrongParameterException("Вы не пользовались вещью");
        }
        Item item = ItemMapper.toItem(getByID(itemId,userId));
        User author = UserMapper.toUser(userService.getById(userId));
        author.setId(userId);
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(itemId)
                .author(userId)
                .created(LocalDateTime.now())
                .build();
        return toCommentDto(commentRepository.save(comment));
    }
    private CommentDto toCommentDto(Comment comment){
        User author = UserMapper.toUser(userService.getById(comment.getAuthor()));

        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .authorName(author.getName())
                .text(comment.getText())
                .build();
    }
    private boolean commentCheck(Long itemId, Long authorId){
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId and b.booker.id = :authorId and b.end < :now and b.status = :status");
        query.setParameter("itemId", itemId);
        query.setParameter("authorId", authorId);
        query.setParameter("now", LocalDateTime.now());
        query.setParameter("status", Status.APPROVED);
        List<Booking> commentBookings = query.list();
        if (commentBookings.size() == 0){
            return false;
        } else {
            return true;
        }
    }

    private List<CommentDto> getCommentsByItem(Long id){
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select c from Comment c where item = :id");
        query.setParameter("id", id);
        List<Comment> comments = query.list();
        System.out.println(comments);
        List<CommentDto> commentDtoList = new ArrayList<>();
        for(Comment c: comments){
            commentDtoList.add(toCommentDto(c));
        }
        return commentDtoList;
    }
    private LastBooking getLastBooking(Long itemId, Long userId){
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId order by b.id asc");
        query.setParameter("itemId", itemId);
        List<Booking> itemBookings = query.list();
        Booking booking = new Booking();
        for (Booking b: itemBookings) {
            if (b.getItem().getOwner() == userId){
                booking = b;
                break;
            }
        }
        LastBooking lastBooking = new LastBooking();
        lastBooking.setId(booking.getId());
        lastBooking.setBookerId(booking.getBooker().getId());
        return lastBooking;
    }
    private NextBooking getNextBooking(Long itemId, Long userId){
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId and b.start > :now order by b.start asc");
        query.setParameter("itemId", itemId);
        query.setParameter("now", LocalDateTime.now());
        List<Booking> itemBookings = query.list();
        Booking booking = new Booking();
        for (Booking b: itemBookings) {
            if (b.getItem().getOwner() == userId){
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
