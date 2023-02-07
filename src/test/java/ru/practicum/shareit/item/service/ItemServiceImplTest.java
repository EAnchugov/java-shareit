package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepositoryJpa;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.itemDto.LastBooking;
import ru.practicum.shareit.item.itemDto.NextBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    ItemService itemService;
    private LastBooking lastbooking = new LastBooking(1L, 1L);

    private NextBooking nextbooking = new NextBooking(2L, 2L);
    private ItemDto.Owner owner = new ItemDto.Owner(1L, "ownerName");
    private List<CommentDto> comments = new ArrayList<>();
    CommentDto commentDto = CommentDto.builder().id(1L).text("commetDto text").authorName("commentDtoAuthor")
            .created(LocalDateTime.of(2022,12,12,12,12,13)).build();

    CommentDto commentDto2 = CommentDto.builder().id(1L).text("commetDto2 text").authorName("commentDtoAuthor2")
            .created(LocalDateTime.of(2022,12,12,13,12,13)).build();
    @Mock
    private UserService us;
    @Mock
    private ItemRepository ir;
    @Mock
    private CommentRepositoryJpa cr;
    @Mock
    private BookingRepository br;
    ItemDto itemDto;

    Item item;
    private UserDto userDto;
    ItemDto updatedDto;

    @BeforeEach
        void setUp() {
        itemService = new ItemServiceImpl(us,ir,cr,br);


    comments.add(commentDto);
    comments.add(commentDto2);

     itemDto = ItemDto.builder()
            .id(1L)
            .name("itemDtoName")
            .description("ItemDtoDescription")
            .available(true)
            .lastBooking(lastbooking)
            .nextBooking(nextbooking)
            .owner(owner)
            .requestId(1L)
            .comments(comments)
            .build();

     item = ItemMapper.toItem(itemDto);

     userDto = UserDto.builder().id(1L).name("user name").email("user@email.org").build();

        updatedDto = itemDto;
        updatedDto.setName("updated name");
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
    void getAll() {
    }

    @Test
    void search() {
    }

    @Test
    void createComment() {
    }
}