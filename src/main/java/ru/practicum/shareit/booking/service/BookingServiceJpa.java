package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.user.service.UserServiceJPA;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.*;

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
        Booking booking = BookingMapper.toBookingFromBookingDto(bookingDto);
        Item item = ItemMapper.toItem(itemServiceImpl.getByID(bookingDto.getItemId()));

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
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(WAITING);
        return BookingMapper.toLongBookingDto(bookingRepository.save(booking));
    }


    @Override
    @Transactional
    public LongBookingDto update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = getBookingById(bookingId);
        Item item = ItemMapper.toItem(itemServiceImpl.getByID(booking.getItem().getId()));
        if (!userId.equals(item.getOwner())) {
            throw new NotFoundException("User не владеет вещью");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new ItemNotAvailableException("Статус != WAITING");
        }

        if (approved.equals(true)){
            booking.setStatus(APPROVED);
        }else {
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toLongBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public List<LongBookingDto> getByOwner(Long userId, String state) {

//        TreeSet<BookingDto> ownerBookings = new TreeSet<>( Comparator.comparing(BookingDto ::getStart));
//        for (Booking booking: bookingRepository.findAll()) {
//            if (itemServiceImpl.getByID(booking.getItem()).getOwner().equals(userId)){
//                ownerBookings.add(BookingMapper.toBookingDtoFromBooking(booking));
//            }
//        }
        return null;
    }

    @Override
    @Transactional
    public List<LongBookingDto> getAllByUser(Long userId, String state) {

        User user = UserMapper.toUser(userServiceJPA.getById(userId));
        user.setId(userId);
        List<Booking> userBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        if (state.equals("ALL")){
            userBookings.addAll(bookingRepository.findAllByBooker(user));
        } else if (state.equals("CURRENT")){
            userBookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                    LocalDateTime.now(), LocalDateTime.now()));
        } else if (state.equals("PAST")){
            userBookings.addAll(bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user,now));
        } else if (state.equals("FUTURE")){
            userBookings.addAll(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user,now));
        } else if (state.equals("WAITING")){
            userBookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(user, WAITING));
        } else if (state.equals("REJECTED")){
            userBookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(user, REJECTED));
        } else {
            throw new WrongParameterException("Неверный state");
        }
//        List<LongBookingDto> userBookingsDto = new ArrayList<>();
//        for (Booking booking: userBookings) {
//            userBookingsDto.add(BookingMapper.toLongBookingDto(booking));
//        }
//        System.out.println(userBookings);
//        System.out.println(userBookingsDto);
        return userBookings.stream().map(BookingMapper::toLongBookingDto).collect(Collectors.toList());
 //       return userBookingsDto;
    }

    @Override
    public LongBookingDto getBookingDtoById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner())){
            throw new WrongParameterException("Не автор бронирования или владелец вещи");
        }
        return BookingMapper.toLongBookingDto(booking);
    }

    private Booking getBookingById(Long id){
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
