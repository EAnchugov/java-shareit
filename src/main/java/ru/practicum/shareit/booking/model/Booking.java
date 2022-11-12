package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import javax.persistence.*;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_time", nullable = false)
    @Future
    private LocalDateTime start;
    @Future
    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;
    private Long item;
    private Long booker;
    private Status status;
}
