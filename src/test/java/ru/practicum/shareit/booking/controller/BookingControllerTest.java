package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingControllerTest.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mvc;
    private LongBookingDto longBookingDto;
    private LongBookingDto.Item item;
    private LongBookingDto.Booker booker;
    private LocalDateTime start = LocalDateTime.of(2023, 12, 8, 8, 0);
    private LocalDateTime finish = LocalDateTime.of(2023, 12, 10, 8, 0);
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        item =new LongBookingDto.Item(1l,"item");
        booker = new LongBookingDto.Booker(1l,"booker");
        longBookingDto = new LongBookingDto(1L,start,finish,
        item,booker, Status.WAITING);
        bookingDto = new BookingDto(1L,start,finish, 1L, 1L, Status.WAITING);

    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), anyLong())).thenReturn(longBookingDto);

        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                .content(mapper.writeValueAsString(bookingDto))
                .header("X-Sharer-User-Id", booker.getId())
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }


    @Test
    void update() {
    }

    @Test
    void getByOwner() {
    }

    @Test
    void getAllByUser() {
    }

    @Test
    void getById() {
    }
}