package ru.practicum.shareit.items.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentClentDto {
    private long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
