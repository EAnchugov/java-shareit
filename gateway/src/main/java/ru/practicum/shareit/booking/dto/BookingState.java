package ru.practicum.shareit.booking.dto;

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
			throw new IllegalArgumentException("Unknown state: " + state);
		}
	}
}
