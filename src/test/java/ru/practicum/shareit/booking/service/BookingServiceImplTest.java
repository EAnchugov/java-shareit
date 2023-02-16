package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
    private ItemDto itemDto2;

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
        WrongParameterException exception1 = assertThrows(WrongParameterException.class,() ->
                bookingService.create(
                        BookingDto.builder().itemId(itemDto.getId()).start(START).end(START.minusYears(1L)).status(Status.APPROVED).bookerId(1L).build(),
                        userDto2.getId()));
        assertEquals(exception1.getMessage(),"Нельзя сдавать в прошлом");


        NotFoundException exception2 = assertThrows(NotFoundException.class,() ->
                bookingService.create(
                        BookingDto.builder().itemId(itemDto.getOwner().getId()).start(START).end(START.minusYears(1L)).status(Status.APPROVED).bookerId(1L).build(),
                        itemDto.getOwner().getId()));
        assertEquals(exception2.getMessage(),"Нельзя бронировать у себя");
    }

    @Test
    void update() {
        user = new User(1L,"name", "mail@mail.org");
        userDto = userService.create(UserMapper.toUserDTO(user));
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        itemDto = itemService.create(ItemDto.builder().name("Item").description("description").available(true).build(),userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto.getId()).start(START).end(END).build();
        longBookingDto = bookingService.create(bookingDto, userDto2.getId());
        longBookingDto2 = bookingService.update(longBookingDto.getId(), itemDto.getOwner().getId(), true);
        assertEquals(longBookingDto2.getItem().getId(), longBookingDto.getItem().getId());

        Throwable throwable2 = assertThrows(Throwable.class, () ->
                bookingService.update(longBookingDto2.getId(), userDto2.getId(), true));
        assertEquals(throwable2.getMessage(), "User не владеет вещью");

        ItemNotAvailableException ex = assertThrows(ItemNotAvailableException.class, () ->
                bookingService.update(longBookingDto.getId(), itemDto.getOwner().getId(), true));
        assertEquals(ex.getMessage(), "Статус != WAITING");

        ItemDto itemDto3 = itemService.create(ItemDto.builder().name("Item").description("description").available(false).build(), userDto.getId());
        bookingDto = BookingDto.builder().itemId(itemDto3.getId()).start(START).end(END).build();
        //System.out.println(itemDto3);
        Throwable throwable = assertThrows(Throwable.class, () ->
                bookingService.create(bookingDto, itemDto.getOwner().getId()));
        assertEquals(throwable.getMessage(), "Вещь недоступна");
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
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,() ->
                bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.ALL), -1,1));
        assertEquals(ex.getMessage(),"From меньше 0");
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,() ->
                bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.ALL), 1,-1));
        assertEquals(exc.getMessage(),"Size меньше 1");
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.ALL), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 1);
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.FUTURE), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 0);
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.PAST), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 0);
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.CURRENT), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 1);
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.WAITING), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 0);
        getAllByOwnerCheck = bookingService.getAllByOwner(itemDto.getOwner().getId(), String.valueOf(BookingState.REJECTED), 0,1);
        assertEquals(getAllByOwnerCheck.size(), 0);
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
        List<LongBookingDto> getAllByUserCheck2 = bookingService.getAllByUser(userDto2.getId(), String.valueOf(BookingState.CURRENT), 0, 1);
        assertEquals(getAllByUserCheck2.size(), 1);
        List<LongBookingDto> getAllByUserCheck3 = bookingService.getAllByUser(userDto2.getId(), String.valueOf(BookingState.PAST), 0, 1);
        assertEquals(getAllByUserCheck3.size(), 0);
        List<LongBookingDto> getAllByUserCheck4 = bookingService.getAllByUser(userDto2.getId(), String.valueOf(BookingState.WAITING), 0, 1);
        assertEquals(getAllByUserCheck4.size(), 0);
        List<LongBookingDto> getAllByUserCheck5 = bookingService.getAllByUser(userDto2.getId(), String.valueOf(BookingState.REJECTED), 0, 1);
        assertEquals(getAllByUserCheck5.size(), 0);
        List<LongBookingDto> getAllByUserCheck6 = bookingService.getAllByUser(userDto2.getId(), String.valueOf(BookingState.FUTURE), 0, 1);
        assertEquals(getAllByUserCheck6.size(), 0);

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
        NotFoundException exc = assertThrows(NotFoundException.class,() ->
                bookingService.getBookingDtoById(longBookingDto.getId(), 99L));
        assertEquals(exc.getMessage(),"Не автор бронирования или владелец вещи");


    }
}