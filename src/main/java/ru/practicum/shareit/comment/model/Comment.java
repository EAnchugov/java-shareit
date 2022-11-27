package ru.practicum.shareit.comment.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

    @ManyToOne
    @JoinColumn(name = "item",referencedColumnName = "id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author",referencedColumnName = "id", nullable = false)
    private User author;
    @Column
    private LocalDateTime created;
}
