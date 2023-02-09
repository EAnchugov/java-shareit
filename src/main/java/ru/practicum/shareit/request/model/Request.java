package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.userDTO.Create;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "description")
    @NotBlank(groups = Create.class)
    private String descriptionRequest;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User requester;
    @NotNull
    private LocalDateTime created;

    @Transient
    List<Item> items = new ArrayList<>();

    @Transient
    RequestAuthor requestAuthor;
}
