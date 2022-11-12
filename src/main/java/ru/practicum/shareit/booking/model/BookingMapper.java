package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking){
        return BookingDto.builder().
                id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .status(booking.getStatus())
                .itemId(booking.getItem())
                .booker(booking.getBooker())
                .build();

    }

    public static Booking dtoToBooking (BookingDto dto){
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(dto.getItemId())
                .booker(dto.getBooker())
                .status(dto.getStatus())
                .build();
    };
}
