package ru.practicum.shareit.comment.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments")
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @Column
    private LocalDateTime created;
}
