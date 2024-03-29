package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@AllArgsConstructor
public class LongBookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private Status status;

    @Data
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }
}
