package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final Long ID_1 = 1L;
    private static final LocalDateTime START = LocalDateTime.now().minusYears(1L);
    private static final LocalDateTime FINISH = LocalDateTime.now().plusYears(1L);
    private static final String NAME = "Name";
    private static final LongBookingDto.Item ITEM = new LongBookingDto.Item(ID_1, NAME);
    private static final LongBookingDto.Booker BOOKER = new LongBookingDto.Booker(ID_1,NAME);
    private static final String BOOKING_URL = "/bookings";
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";
    private static BookingDto bookingDto;
    private static LongBookingDto longBookingDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    private List<LongBookingDto> longBookingDtoList;

    @BeforeEach
    void setUp() {
        longBookingDto = new LongBookingDto(ID_1, START, FINISH, ITEM, BOOKER, Status.WAITING);
        bookingDto = new BookingDto(ID_1,FINISH,FINISH.plusYears(1L),ID_1,ID_1,Status.WAITING);
    }

    @Test
    void create() throws Exception {
        Mockito
                .when(bookingService.create(any(), any())).thenReturn(longBookingDto);

        mockMvc.perform(post(BOOKING_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(SHARER_USER_ID, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", equalTo(NAME)));
    }

    @Test
    void update() throws Exception {
        Mockito
                .when(bookingService.update(any(), any(), any())).thenReturn(longBookingDto);

        mockMvc.perform(patch(BOOKING_URL + "/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto))
                        .header(SHARER_USER_ID, 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.name", equalTo(NAME)));
    }


    @Test
    void getByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(longBookingDtoList);

        mockMvc.perform(get(BOOKING_URL + "?from=0&size=2")
                        .header(SHARER_USER_ID, 1)
                )
                .andExpect(status().isOk());
    }

    @Test
    void getAllByUser() {
    }

    @Test
    void getById() {
    }
}