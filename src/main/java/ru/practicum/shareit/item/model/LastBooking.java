package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor
public class LastBooking {
    private Long id;
    private Long bookerId;
}
