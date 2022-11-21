package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class LastBooking {
    private Long id;
    private Long bookerId;
}
