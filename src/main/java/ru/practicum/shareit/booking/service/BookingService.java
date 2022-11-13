package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;

import java.util.List;
import java.util.TreeSet;

public interface BookingService {
    LongBookingDto create(BookingDto bookingDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    TreeSet<BookingDto> getAllByOwner(Long userId, String state);

    List<BookingDto> getAllByUser(Long userId, String state);

    BookingDto getById(Long bookingId, Long userId);
}
