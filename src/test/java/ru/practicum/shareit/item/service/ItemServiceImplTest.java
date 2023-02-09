package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final RequestService requestService;
    private User user;
    private UserDto userDto;
    private ItemDto itemDto;
    private LongBookingDto.Item item;
    private CommentDto commentDto;
    private BookingDto bookingDto;
    private User user2;
    private UserDto userDto2;
    private RequestDtoInput input;


    @Test
    void create() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        ItemDto check = itemService.create(itemDto, itemDto.getOwner().getId());
        assertThat(check.getId(),notNullValue());
        assertEquals(check.getName(),itemDto.getName());
        itemDto = check;
    }

    @Test
    void update() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("description").available(true).build(),userDto.getId());
        itemDto = itemService.create(itemDto, itemDto.getOwner().getId());
        itemDto.setName("updated name");
        ItemDto check = itemService.update(itemDto,itemDto.getOwner().getId(),itemDto.getId());
        assertEquals(check.getName(), itemDto.getName());
    }

    @Test
    void getByID() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("description").available(true).build(),userDto.getId());
        ItemDto check = itemService.getByID(itemDto.getId(), userDto.getId());
        assertEquals(check.getOwner().getId(), userDto.getId());
    }

    @Test
    void getAllByOwnerId() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("description").available(true).build(),userDto.getId());
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("description").available(true).build(),userDto.getId());
        List<ItemDto> check = itemService.getAllByOwnerId(user.getId());
        assertEquals(2,check.size());
    }

    @Test
    void search() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("description").available(true).build(),userDto.getId());
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("search").available(true).build(),userDto.getId());
        List<ItemDto> check = itemService.search("search");
        assertEquals(check.get(0).getDescription(), "search");

    }

    @Test
    void createComment() {
        //Long itemId, Long userId, CommentDto commentDto
        user = new User(1L,"name", "mail@mail.org");
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item")
                .description("description").available(true).build(),userDto.getId());
        commentDto = CommentDto.builder().text("comment").build();
        bookingDto = new BookingDto(1L, LocalDateTime.now().minusYears(1L),
                LocalDateTime.now().plusYears(1L),itemDto.getId(),userDto2.getId(), Status.APPROVED);
        bookingService.create(bookingDto, userDto2.getId());

        Throwable thrown = assertThrows(WrongParameterException.class, () -> {
            itemService.createComment(user.getId(),
                    itemDto.getId(), commentDto);
        });
        assertThat(thrown.getMessage(),
                equalTo("Вы не пользовались вещью"));
    }

    @Test
    void getItemsByRequest() {
        user = new User(1L,"name", "mail@mail.org");
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        input = RequestDtoInput.builder().description("Хотел бы воспользоваться щёткой для обуви").build();
        requestService.create(userDto.getId(), input);
        itemDto = itemService.create(ItemDto.builder().requestId(1L).name("Item")
                .description("description").available(true).build(),userDto.getId());
        List<Item> request = itemService.getItemsByRequest(1L);
        assertEquals(request.get(0).getId(), itemDto.getId());
    }
}