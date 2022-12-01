package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.exceptions.WrongParameterException;

public enum BookingState {
    ALL,
    FUTURE,
    PAST,
    CURRENT,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongParameterException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
