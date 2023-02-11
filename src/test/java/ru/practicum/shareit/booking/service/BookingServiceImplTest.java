package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
@SpringBootTest(
        properties = "db.name=BookingServiceImplTest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private static final LocalDateTime START =
            LocalDateTime.of(2022,12,12,12,12,12);
    private static final LocalDateTime END =
            LocalDateTime.of(2023,12,12,12,12,12);
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private User user;
    private UserDto userDto;
    private BookingDto bookingDto;
    private ItemDto itemDto;
    private LongBookingDto longBookingDto;
    private User user2;
    private UserDto userDto2;
    private LongBookingDto longBookingDto2;
    private List<LongBookingDto> getAllByOwnerCheck;
    private LongBookingDto getBookingDtoByIdCheck;

    @BeforeEach
    void setup(){
//        userDto = UserDto.builder().id(1l).name("user").email("user@mail.org").build();
    }

    @Test
    void create() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto.getId()).start(START).status(Status.APPROVED).bookerId(1L).end(END).build();
        bookingDto.setStart(START);
        longBookingDto = bookingService.create(bookingDto, userDto2.getId());
        assertEquals(longBookingDto.getItem().getId(), itemDto.getId());
        System.out.println(bookingDto.getStatus());
        System.out.println(bookingDto.getBookerId());
        System.out.println(bookingDto.getItemId());
    }

    @Test
    void update() {
        // TODO: 10.02.2023 юзер не владеет вешью
        // TODO: 10.02.2023 статусы
        user = new User(1L,"name", "mail@mail.org");
        User errorUser = User.builder().name("name").email("mail@mail.org").build();
        userDto = userService.create(UserMapper.toUserDTO(user));
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto.getId()).start(START).end(END).build();
        longBookingDto = bookingService.create(bookingDto, userDto2.getId());
        longBookingDto2 = bookingService.update(longBookingDto.getId(), itemDto.getOwner().getId(), true);
        assertEquals(longBookingDto2.getItem().getId(), longBookingDto.getItem().getId());
    }

    @Test
    void getAllByOwner() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto.getId()).start(START).end(END).build();
        longBookingDto = bookingService.create(bookingDto, userDto2.getId());
        longBookingDto2 = bookingService.update(longBookingDto.getId(), itemDto.getOwner().getId(), true);
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.ALL), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 1);
    }

    @Test
    void getAllByUser() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto.getId()).start(START).end(END).build();
        longBookingDto = bookingService.create(bookingDto, userDto2.getId());
        longBookingDto2 = bookingService.update(longBookingDto.getId(), itemDto.getOwner().getId(), true);
        List<LongBookingDto> getAllByUserCheck = bookingService.getAllByUser(userDto2.getId(), String.valueOf(BookingState.ALL), 0, 1);
        assertEquals(getAllByUserCheck.size(),1);
        assertEquals(getAllByUserCheck.get(0).getBooker().getId(), userDto2.getId());
    }

    @Test
    void getBookingDtoById() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto.getId()).start(START).end(END).build();
        longBookingDto = bookingService.create(bookingDto, userDto2.getId());
        getBookingDtoByIdCheck = bookingService.getBookingDtoById(longBookingDto.getId(), userDto.getId());
        assertEquals(getBookingDtoByIdCheck.getId(), longBookingDto.getId());

    }
}