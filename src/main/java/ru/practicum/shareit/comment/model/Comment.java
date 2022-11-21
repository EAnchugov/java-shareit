package ru.practicum.shareit.comment.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@Entity()
@Table(name = "comments")

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "comment_text",length = 1000, nullable = false)
    private String text;

    @Column(nullable = false)
    private Long item;

    @Column(nullable = false)
    private Long author;

    private LocalDateTime created;
}
