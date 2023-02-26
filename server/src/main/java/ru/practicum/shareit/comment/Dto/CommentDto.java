package ru.practicum.shareit.comment.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
