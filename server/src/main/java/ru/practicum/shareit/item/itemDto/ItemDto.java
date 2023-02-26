package ru.practicum.shareit.item.itemDto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.comment.Dto.CommentDto;

import java.util.List;

@Data
@Slf4j
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LastBooking lastBooking;
    private NextBooking nextBooking;
    private Owner owner;
    private Long requestId;
    private List<CommentDto> comments;

    @Data
    public static class Owner {
        private final long id;
        private final String name;
    }
}
