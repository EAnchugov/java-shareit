package ru.practicum.shareit.items.controller;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CommentDto {
    private long id;
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
