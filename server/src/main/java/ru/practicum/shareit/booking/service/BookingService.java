package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;

import java.util.List;

public interface BookingService {
    LongBookingDto create(BookingDto bookingDto, Long userId);

    LongBookingDto update(Long bookingId, Long userId, Boolean approved);

    List<LongBookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size);

    List<LongBookingDto> getAllByUser(Long userId, String state, Integer from,Integer size);

    LongBookingDto getBookingDtoById(Long bookingId, Long userId);
}
