package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Slf4j
@Builder
public class RequestDtoOut {
    private Long id;
    private String descriptionRequest;
    private User requestor;
    private LocalDateTime created;
}
