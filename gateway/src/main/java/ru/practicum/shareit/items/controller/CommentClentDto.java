package ru.practicum.shareit.items.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentClentDto {
    private long id;
    @NotBlank
    @NotNull
    private String text;
    private String authorName;
    private LocalDateTime created;
}
