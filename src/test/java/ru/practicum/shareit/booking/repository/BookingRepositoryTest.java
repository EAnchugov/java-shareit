package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import(ShareItApp.class)
@Sql({"/test-schema.sql", "/test-data.sql"})
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    private List<Item> items = new ArrayList<>();
    private List<Booking> check;
    private Item item;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findApprovedForItems() {
//        List<Booking> findApprovedForItems(List< Item > items, Sort sort);
        item = Item.builder().id(1L).name("name").description("description").available(true).build();
        items.add(item);
        check = bookingRepository.findApprovedForItems(items, Sort.by(Sort.Direction.DESC, "start"));
        Assertions.assertEquals(check.size(), 1);




    }
}