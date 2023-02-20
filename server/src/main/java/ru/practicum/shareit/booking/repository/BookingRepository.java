package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerOrderByIdDesc(User owner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByIdDesc(User owner, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByIdDesc(User owner, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndAfterAndStartBeforeOrderByIdDesc(
            User owner, LocalDateTime end, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStatusEqualsOrderByIdDesc(User owner, Status status, Pageable pageable);

    List<Booking> findAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User user,
                                                                           LocalDateTime start, LocalDateTime end,
                                                                           Pageable pageable);

    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User user, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User user, Status status, Pageable pageable);

   @Query("select b from Booking b where b.item in ?1 and b.status = 'APPROVED' order by b.item.id")
    List<Booking> findApprovedForItems(List<Item> items);



}
