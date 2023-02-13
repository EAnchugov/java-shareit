package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LongBookingDtoTest {
    LongBookingDto longBookingDto = LongBookingDto.builder().build();

    @Test
    void getStart() {
        assertEquals(longBookingDto.getStart(), null);
    }

    @Test
    void getEnd() {
        assertEquals(longBookingDto.getEnd(), null);
    }

    @Test
    void getStatus() {
        assertEquals(longBookingDto.getStatus(), null);
    }

    @Test
    void setStart() {
        longBookingDto.setStart(null);
    }

    @Test
    void setEnd() {
        longBookingDto.setEnd(null);
        assertEquals(longBookingDto.getEnd(),null);
    }

    @Test
    void setItem() {
        longBookingDto.setItem(new LongBookingDto.Item(1L,null));
        assertEquals(longBookingDto.getItem().getName(), null);
    }

    @Test
    void setBooker() {
        longBookingDto.setBooker(new LongBookingDto.Booker(1L, null));
        assertEquals(longBookingDto.getBooker().getName(), null);
    }

    @Test
    void setStatus() {
        longBookingDto.setStatus(null);
    }
}