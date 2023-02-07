package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.request.model.RequestAuthor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@Builder
public class RequestDtoOut {
    private Long id;
    private String description;
//    private User requestor;
    private LocalDateTime created;
    private List<ItemDto> items = new ArrayList<>();
    private RequestAuthor requestAuthor;

}
