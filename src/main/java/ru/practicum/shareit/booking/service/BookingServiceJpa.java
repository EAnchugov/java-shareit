package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;
import ru.practicum.shareit.user.service.UserServiceJPA;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.booking.Status.*;
import static ru.practicum.shareit.booking.model.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
public class BookingServiceJpa implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceJPA userServiceJPA;
    private final ItemServiceImpl itemServiceImpl;
    @Override
    @Transactional
    public LongBookingDto create(BookingDto bookingDto, Long userId) {
        User user = UserMapper.toUser(userServiceJPA.getById(userId));
        user.setId(userId);
        Booking booking = BookingMapper.dtoToBooking(bookingDto);
        Item item = ItemMapper.toItem(itemServiceImpl.getByID(bookingDto.getItemId()));
        item.setId(bookingDto.getItemId());

        if (!item.getAvailable()){
            throw new ItemNotAvailableException("Вещь недоступна");
        }
        if (item.getOwner().equals(userId)){
            throw new WrongParameterException("Нельзя бронировать у себя");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongParameterException("нельзя бронировать в прошлом");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new WrongParameterException("Нельзя сдавать в прошлом");
        }
        if (booking.getStart().isAfter(booking.getEnd())){
            throw new WrongParameterException("Нельзя сдавать раньше чем получить");
        }

        booking.setBooker(user.getId());
        booking.setItem(item.getId());
        booking.setStatus(WAITING);
        bookingRepository.save(booking);
//        return LongBookingDto.builder()
//                .id(booking.getId())
//                .start(booking.getStart())
//                .end(booking.getEnd())
//                .item(item)
//                .booker(user)
//                .status(booking.getStatus())
//                .build();
        return longBookingDtoCreator(booking,userId);
    }

    @Override
    public LongBookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        User user = UserMapper.toUser(userServiceJPA.getById(userId));
        user.setId(userId);
        Item item = ItemMapper.toItem(itemServiceImpl.getByID(booking.getItem()));
        item.setId(booking.getItem());

        if (!userId.equals(item.getOwner())) {
            throw new NotFoundException("Невозможно подтвердить бронирование - " +
                    "не найдено бронирование с id " + bookingId + " у пользователя с id" + userId);
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new ItemNotAvailableException("Невозможно подтвердить бронирование - " +
                    "бронирование уже подтверждено или отклонено");
        }

        if (approved == true){
            booking.setStatus(APPROVED);
        }else {
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);
        return longBookingDtoCreator(booking,userId);

//        return LongBookingDto.builder()
//                .id(booking.getId())
//                .start(booking.getStart())
//                .end(booking.getEnd())
//                .item(item)
//                .booker(user)
//                .status(booking.getStatus())
//                .build();
    }

    @Override
    @Transactional
    public TreeSet<BookingDto> getAllByOwner(Long userId, String state) {
        TreeSet<BookingDto> ownerBookings = new TreeSet<>( Comparator.comparing(BookingDto ::getStart));
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
    private LongBookingDto longBookingDtoCreator(Booking booking, Long userId){
        Item item = ItemMapper.toItem(itemServiceImpl.getByID(booking.getItem()));
        item.setId(booking.getItem());
        User user = UserMapper.toUser(userServiceJPA.getById(userId));
        user.setId(userId);
        return LongBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(item)
                .booker(user)
                .status(booking.getStatus())
                .build();

    }
}
