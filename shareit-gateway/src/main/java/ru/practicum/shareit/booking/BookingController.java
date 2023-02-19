package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestParam(name = "state", defaultValue = "ALL") String state,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
			@RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}
	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> setStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
											@PathVariable Long bookingId,@RequestParam Boolean approved) {
		log.info("Получен PATCH запрос к эндпоинту: '/bookings', Строка параметров запроса:" +
				" userId = {}, bookingId = {}, approved = {}", userId, bookingId, approved);
		return bookingClient.update(userId, bookingId, approved);
	}
	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
										   @RequestParam(defaultValue = "ALL") String state,
										   @PositiveOrZero @RequestParam(value = "from",
												   defaultValue = "0") Integer from,
										   @Positive @RequestParam(value = "size",
												   defaultValue = "20") Integer size) {
		return bookingClient.getAllByOwner(userId, state, from, size);
	}

}
