package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner(User owner);

    @Query("select b from Booking b left join fetch b.item AS i where i.id = :itemId " +
            "and b.booker.id = :authorId and b.end < :now and b.status = :status")
    List<Booking> commentCheck(Long itemId, Long authorId, LocalDateTime now, Status status);

    @Query("select b from Booking b left join fetch b.item AS i where i.id = :itemId order by b.id asc")
    List<Booking> getItemBookings(Long itemId);

    @Query("select b from Booking b left join fetch b.item AS i where i.id = :itemId and b.start > :now order by b.start asc")
    List<Booking> getItemNextBooking(Long itemId, LocalDateTime now);

    List<Item> getByRequestOrderById(Long request);

}
