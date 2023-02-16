package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.LongBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PaginationCheck;
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
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.booking.Status.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final PaginationCheck paginationCheck;



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
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new WrongParameterException("Нельзя сдавать в прошлом");
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
        return BookingMapper.toLongBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<LongBookingDto> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        paginationCheck.paginationCheck(from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(DESC, "start"));
        LocalDateTime now = LocalDateTime.now();
        User user = UserMapper.toUser(userService.getById(userId));
        user.setId(userId);
        List<Booking> ownerBookings = new ArrayList<>();
        switch (BookingState.from(state)) {
            case ALL:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerOrderByIdDesc(user, pageable));
                break;
            case FUTURE:
                ownerBookings.addAll(
                        bookingRepository.findAllByItemOwnerAndStartAfterOrderByIdDesc(user,now, pageable));
                break;
            case PAST:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerAndEndBeforeOrderByIdDesc(user,now, pageable));
                break;
            case CURRENT:
                ownerBookings.addAll(
                bookingRepository.findAllByItemOwnerAndEndAfterAndStartBeforeOrderByIdDesc(user,now, now, pageable));
                break;
            case WAITING:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByIdDesc(user,WAITING, pageable));
                break;
            case REJECTED:
                ownerBookings.addAll(bookingRepository.findAllByItemOwnerAndStatusEqualsOrderByIdDesc(user,REJECTED, pageable));
                break;
        }
        return ownerBookings.stream().map(BookingMapper::toLongBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<LongBookingDto> getAllByUser(Long userId, String state, Integer from, Integer size) {
        paginationCheck.paginationCheck(from, size);
        User user = UserMapper.toUser(userService.getById(userId));
        user.setId(userId);
        List<Booking> userBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(DESC, "start"));
        switch (BookingState.from(state)) {
            case ALL:
                userBookings.addAll(bookingRepository.findAllByBookerOrderByStartDesc(user,pageable));
                break;
            case CURRENT:
                userBookings.addAll(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case PAST:
                userBookings.addAll(bookingRepository.findByBookerAndEndBeforeOrderByStartDesc(user,now, pageable));
                break;
            case FUTURE:
                userBookings.addAll(bookingRepository.findByBookerAndStartAfterOrderByStartDesc(user,now, pageable));
                break;
            case WAITING:
                userBookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(user, WAITING, pageable));
                break;
            case REJECTED:
                userBookings.addAll(bookingRepository.findByBookerAndStatusOrderByStartDesc(user, REJECTED, pageable));
                break;
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
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет вещи с id =" + id));
        return booking;
    }
}
