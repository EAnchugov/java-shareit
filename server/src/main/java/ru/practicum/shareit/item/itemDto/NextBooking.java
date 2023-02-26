package ru.practicum.shareit.item.itemDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextBooking {
    private Long id;
    private Long bookerId;
}
