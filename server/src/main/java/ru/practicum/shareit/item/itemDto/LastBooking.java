package ru.practicum.shareit.item.itemDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastBooking {
    private Long id;
    private Long bookerId;
}
