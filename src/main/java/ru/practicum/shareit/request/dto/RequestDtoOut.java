package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.request.model.RequestAuthor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Slf4j
@Builder
public class RequestDtoOut {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
    private RequestAuthor requestAuthor;

}
