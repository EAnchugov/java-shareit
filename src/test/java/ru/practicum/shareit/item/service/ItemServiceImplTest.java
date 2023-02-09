package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepositoryJpa;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.itemDto.LastBooking;
import ru.practicum.shareit.item.itemDto.NextBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserService us;
    @Mock
    private ItemRepository ir;
    @Mock
    private CommentRepositoryJpa cr;
    @Mock
    private BookingRepository br;

    ItemService itemService;
    Item item;
    ItemDto itemDto;
    UserDto userDto;
    User user;
    ItemDto updatedDto;
    private LastBooking lastbooking = new LastBooking(1L, 1L);

    private NextBooking nextbooking = new NextBooking(2L, 2L);
    private ItemDto.Owner owner = new ItemDto.Owner(1L, "ownerName");
    private List<CommentDto> commentDtos = new ArrayList<>();
    private  Map<Item, List<Booking>> map = new HashMap<>();
    CommentDto commentDto = CommentDto.builder().id(1L).text("commetDto text").authorName("commentDtoAuthor")
            .created(LocalDateTime.of(2022,12,12,12,12,13)).build();

    CommentDto commentDto2 = CommentDto.builder().id(2L).text("commetDto2 text").authorName("commentDtoAuthor2")
            .created(LocalDateTime.of(2022,12,12,13,12,13)).build();

    Comment comment1 = Comment.builder().id(1L).text("commet text").author(user)
            .created(LocalDateTime.of(2022,12,12,12,12,13)).build();

    Comment comment2 = Comment.builder().id(2L).text("commet2 text").author(user)
            .created(LocalDateTime.of(2022,12,12,13,12,13)).build();


    Sort sort = Sort.by(Sort.Direction.DESC, "start");
    private List<Item> items;
    private List<Booking> bookings;
    private Booking booking1;
    private List<Comment> comments;


    @BeforeEach
        void setUp() {
        itemService = new ItemServiceImpl(us,ir,cr,br);


    commentDtos.add(commentDto);
    commentDtos.add(commentDto2);

     itemDto = ItemDto.builder()
            .id(1L)
            .name("itemDtoName")
            .description("ItemDtoDescription")
            .available(true)
            .lastBooking(lastbooking)
            .nextBooking(nextbooking)
            .owner(owner)
            .requestId(1L)
            .comments(commentDtos)
            .build();

     item = ItemMapper.toItem(itemDto);
     items.add(item);

     userDto = UserDto.builder().id(1L).name("user name").email("user@email.org").build();
     user = UserMapper.toUser(userDto);

     updatedDto = itemDto;
     updatedDto.setName("updated name");
     bookings.add(booking1);
     booking1 = new Booking();
     comments.add(comment1);
     map.put(item, bookings);
    }



    @Test
    void create() {
        when(ir.save(any())).thenReturn(item);
        when(us.getById(anyLong())).thenReturn(userDto);

        ItemDto id = itemService.create(itemDto, 1L);
        Assertions.assertEquals(id.getDescription(), itemDto.getDescription());
    }

    @Test
    void update() {
        when(ir.save(any())).thenReturn(item);
        when(ir.findById(anyLong())).thenReturn(Optional.of(item));
        when(ir.save(any())).thenReturn(item);
        when(us.getById(anyLong())).thenReturn(userDto);
        ItemDto id = itemService.update(updatedDto, 1L,1L);
        Assertions.assertEquals(updatedDto.getName(), id.getName());
    }

    @Test
    void getByID() {
        when(ir.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto find = itemService.getByID(1L,1L);
        Assertions.assertEquals(find.getName(), item.getName());

    }

    @Test
    void getAllByOwnerId() {
//        when(ir.findAllByOwner(any())).thenReturn(items);
//        when(br.findApprovedForItems(items, sort)).thenReturn(bookings);
//        when(cr.findAllByItemIn(items,sort)).thenReturn(comments);
//
//        List<ItemDto> i = itemService.getAllByOwnerId(1L);
//        assertEquals(1, i.size());


     }

    @Test
    void search() {
    }

    @Test
    void createComment() {
//        when(us.getById(anyLong())).thenReturn(userDto);
//        when(ir.getById(anyLong())).thenReturn(item);
//        CommentDto commentDto1 =itemService.createComment(1L, 1L, commentDto);
//        assertEquals(commentDto1.getText(), "123123");
    }
}