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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.Status.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;



    @Override
    @Transactional
    public LongBookingDto create(BookingDto bookingDto, Long userId) {
        User user = UserMapper.toUser(userService.getById(userId));
        user.setId(userId);
        Booking booking = BookingMapper.toBookingFromBookingDto(bookingDto);
        Item item = ItemMapper.toItem(itemService.getByID(bookingDto.getItemId(), userId));
        Long ownerId = item.getOwner().getId();

        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь недоступна");
        }
        if (ownerId.equals(userId)) {
            throw new NotFoundException("Нельзя бронировать у себя");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongParameterException("нельзя бронировать в прошлом");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new WrongParameterException("Нельзя сдавать в прошлом");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
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
        Item item = ItemMapper.toItem(itemService.getByID(booking.getItem().getId(), userId));
        if (!userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("User не владеет вещью");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new ItemNotAvailableException("Статус != WAITING");
        }
        if (approved.equals(true)) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.toLongBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<LongBookingDto> getAllByOwner(Long userId, String state) {
        LocalDateTime now = LocalDateTime.now();
        User user = UserMapper.toUser(userService.getById(userId));
        user.setId(userId);
        List<Booking> ownerBookings = new ArrayList<>();
        switch (BookingState.from(state)) {
            case ALL:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerOrderByIdDesc(user));
                break;
            case FUTURE:
                ownerBookings.addAll(
                        bookingRepository.findAllByItemOwnerAndStartAfterOrderByIdDesc(user,now));
                break;
            case PAST:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerAndEndBeforeOrderByIdDesc(user,now));
                break;
            case CURRENT:
                ownerBookings.addAll(
                bookingRepository.findAllByItemOwnerAndEndAfterAndStartBeforeOrderByIdDesc(user,now, now));
                break;
            case WAITING:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByIdDesc(user,WAITING));
                break;
            case REJECTED:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByIdDesc(user,REJECTED));
                break;
//            default:
//                throw new WrongParameterException("Unknown state: UNSUPPORTED_STATUS");
        }
        return ownerBookings.stream().map(BookingMapper::toLongBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<LongBookingDto> getAllByUser(Long userId, String state) {
        User user = UserMapper.toUser(userService.getById(userId));
        user.setId(userId);
        List<Booking> userBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (BookingState.from(state)) {
            case ALL:
                userBookings.addAll(bookingRepository.findAllByBookerOrderByStartDesc(user));
                break;
            case CURRENT:
                userBookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                userBookings.addAll(bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user,now));
                break;
            case FUTURE:
                userBookings.addAll(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user,now));
                break;
            case WAITING:
                userBookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(user, WAITING));
                break;
            case REJECTED:
                userBookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(user, REJECTED));
                break;
//            default:
//                throw new WrongParameterException("Unknown state: UNSUPPORTED_STATUS");
        }
        return userBookings.stream().map(BookingMapper::toLongBookingDto).collect(Collectors.toList());
    }

    @Override
    public LongBookingDto getBookingDtoById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Не автор бронирования или владелец вещи");
        }
        return BookingMapper.toLongBookingDto(booking);
    }

    private Booking getBookingById(Long id) {
        Booking booking;
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            booking = optionalBooking.get();
        } else {
            throw new NotFoundException("Нет вещи с id =" + id);
        }
        return booking;
    }
}