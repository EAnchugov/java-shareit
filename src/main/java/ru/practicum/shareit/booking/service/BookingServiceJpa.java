package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.*;

import static ru.practicum.shareit.booking.model.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
public class BookingServiceJpa implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemServiceImpl itemServiceImpl;
    @Override
    @Transactional
    public BookingDto create(BookingDto bookingDto, Long userId) {
        Item item = ItemMapper.toItem(itemServiceImpl.getByID(userId));
        if (item.getAvailable().equals(false)){
            throw new ItemNotAvailableException("Вещь недоступна");
        }
        return toBookingDto(bookingRepository.save(BookingMapper.dtoToBooking(bookingDto)));
    }

    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        return BookingMapper.toBookingDto(getBooking(bookingId));
    }

    @Override
    @Transactional
    public TreeSet<BookingDto> getAllByOwner(Long userId, String state) {
        TreeSet<BookingDto> ownerBookings = new TreeSet<BookingDto>( Comparator.comparing(BookingDto ::getStart));
        for (Booking booking: bookingRepository.findAll()) {
            if (itemServiceImpl.getByID(booking.getItem()).getOwner().equals(userId)){
                ownerBookings.add(toBookingDto(booking));
            }
        }
        return ownerBookings;
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, String state) {
        List<BookingDto> userBooking = new ArrayList<>();
        for (Booking booking : bookingRepository.findAll()) {
            if (booking.getBooker().equals(userId)){
                userBooking.add(toBookingDto(booking));
            }
        }
        return userBooking;
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        return null;
    }

    private Booking getBooking(Long id){
        Booking booking;
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if(optionalBooking.isPresent()){
            booking = optionalBooking.get();
        }
        else {
            throw new NotFoundException("Нет вещи с id =" + id);
        }
        return booking;
    }
}
