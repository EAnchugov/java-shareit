package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.UnavailableException;

@Service
@RequiredArgsConstructor
public class BookingServiceJpa implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        Item item = ItemMapper.toItem(itemService.getByID(userId));
        if (item.getAvailable().equals(false)){
            throw new ItemNotAvailableException("Вещь недоступна");
        }
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.dtoToBooking(bookingDto)));
    }
}
