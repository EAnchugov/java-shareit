package ru.practicum.shareit.item.model;

import lombok.*;

import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @ManyToOne(optional = false)
    @JoinColumn(name = "OWNER", nullable = false)
    private User owner;
    @Column(nullable = false)
    private Long request;
}
