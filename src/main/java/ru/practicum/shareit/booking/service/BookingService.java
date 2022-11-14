package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;

import java.util.List;
import java.util.TreeSet;

public interface BookingService {
    LongBookingDto create(BookingDto bookingDto, Long userId);

    LongBookingDto approve(Long bookingId, Long userId, Boolean approved);

    TreeSet<BookingDto> getAllByOwner(Long userId, String state);

    List<LongBookingDto> getAllByUser(Long userId, String state);

    LongBookingDto getBookingDtoById(Long bookingId, Long userId);
}
