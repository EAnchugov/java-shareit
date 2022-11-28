package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerOrderByIdDesc(User owner);
    List<Booking> findAllByItemOwnerAndStartAfterOrderByIdDesc(User owner, LocalDateTime start);
    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user,
                                                                           LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User user, Status status);

}
