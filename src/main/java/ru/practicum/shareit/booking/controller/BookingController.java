package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;
import java.util.TreeSet;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public LongBookingDto create(@Valid @RequestBody BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId){
        return bookingService.create(bookingDto, userId);
    }
    @PatchMapping("/{bookingId}")
    public LongBookingDto update(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam Boolean approved){
        return bookingService.update(bookingId, userId,approved);
    }

    @GetMapping("/owner")
    public List<LongBookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByOwner(userId, state);
    }

    @GetMapping
    public List<LongBookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/{bookingId}")
    public LongBookingDto getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingDtoById(bookingId, userId);
    }
}
