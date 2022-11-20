package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.LastBooking;
import ru.practicum.shareit.item.model.NextBooking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepositoryJpa itemRepositoryJpa;
    private final EntityManager entityManager;
    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkUser(userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemRepositoryJpa.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto dto, Long userId, Long itemId) {
        checkUser(userId);
        Item item = ItemMapper.toItem(getByID(itemId, userId));
        if (item != null) {
            if (!(item.getOwner().equals(userId))) {
                throw new NotFoundException("Изменять может только владелец");
            }
            if (dto.getAvailable() != null) {
                item.setAvailable(dto.getAvailable());
            }
            if (dto.getName() != null && !(dto.getName().isBlank())) {
                item.setName(dto.getName());
            }
            if (dto.getDescription() != null && !(dto.getDescription().isBlank())) {
                item.setDescription(dto.getDescription());
            }
            if (dto.getOwner() != null) {
                item.setOwner(dto.getOwner());
            }
            if (dto.getRequest() != null) {
                item.setRequest(dto.getRequest());
            }

            } else {
                throw  new WrongParameterException("Вещь не найдена");
           }
        item.setId(itemId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemRepositoryJpa.save(item));
    }

    @Override
    public ItemDto getByID(Long id, Long userId) {
        Item item;
        Optional<Item> optionalItem = itemRepositoryJpa.findById(id);
        if(optionalItem.isPresent()){
            item = optionalItem.get();
        }
        else {
            throw new NotFoundException("Нет вещи с id =" + id);
        }
        ItemDto itemDto = ItemMapper.toItemDto(item);
        try {
            itemDto.setLastBooking(getLastBooking(id, userId));
            itemDto.setNextBooking(getNextBooking(id, userId));
        }finally {
            return itemDto;
        }
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
            List<ItemDto> items = new ArrayList<>();
            for (Item item : itemRepositoryJpa.findAll()) {
                if (item.getOwner().equals(userId)) {
                    items.add(ItemMapper.toItemDto(item));
                }
            }
        return items;
    }

    @Override
    public List<ItemDto> search(String request) {
        ArrayList<ItemDto> items = new ArrayList<>();
        if (!request.isBlank()) {
            for (Item item :itemRepositoryJpa.findAll()) {
                if (item.getAvailable() &&
                        item.getDescription().toLowerCase().contains(request.toLowerCase()) ||
                        item.getName().toLowerCase().contains(request.toLowerCase())) {
                    items.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return items;
    }

    private LastBooking getLastBooking(Long itemId, Long userId){
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId order by b.id asc");
        query.setParameter("itemId", itemId);
        List<Booking> itemBookings = query.list();
        Booking booking = itemBookings.get(0);
        LastBooking lastBooking = new LastBooking();
        lastBooking.setId(booking.getId());
        lastBooking.setBookerId(booking.getBooker().getId());
        return lastBooking;
    }
    private NextBooking getNextBooking(Long itemId, Long userId){
        Session session = entityManager.unwrap(Session.class);
        Query query;
        query = session.createQuery("select b from Booking b left join fetch b.item AS i" +
                " where i.id = :itemId and b.start > :now order by b.start asc");
        query.setParameter("itemId", itemId);
        query.setParameter("now", LocalDateTime.now());
        List<Booking> itemBookings = query.list();
        Booking booking = itemBookings.get(0);
        NextBooking nextBooking = new NextBooking();
        nextBooking.setId(booking.getId());
        nextBooking.setBookerId(booking.getBooker().getId());
        return nextBooking;
    }


    private void checkUser(Long userId) {
        try {
            userService.getById(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Юзер с ID " + userId + " не найден " + e.getLocalizedMessage());
        }
    }
}
