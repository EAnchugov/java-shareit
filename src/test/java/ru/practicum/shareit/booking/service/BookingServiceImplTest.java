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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.of(2023,12,12,12,12),
            LocalDateTime.of(2022,12,12,12,12),
            1L,1L, Status.WAITING);
    User user;
    UserDto userDto = UserDto.builder().id(1l).name("user").email("user@mail.org").build();;
    LongBookingDto longBookingDto;
    private ItemDto itemDto = ItemDto.builder()
            .name("itemDto").description("itemDto description").available(true).owner(new ItemDto.Owner(userDto.getId(), userDto.getName())).requestId(1L).build();
 //   available, description, name, owner, request

    @BeforeEach
    void setup(){
//        userDto = UserDto.builder().id(1l).name("user").email("user@mail.org").build();
    }

    @Test
    void create() {
//      userDto = userService.create(userDto);
//      itemDto = itemService.create(itemDto, userDto.getId());
//      longBookingDto =  bookingService.create(bookingDto, userDto.getId());
//      assertEquals(longBookingDto.getBooker().getId(), userDto.getId());


    }

    @Test
    void update() {
    }

    @Test
    void getAllByOwner() {
    }

    @Test
    void getAllByUser() {
    }

    @Test
    void getBookingDtoById() {
    }
}