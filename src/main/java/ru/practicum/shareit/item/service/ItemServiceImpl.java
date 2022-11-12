package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryJpa;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepositoryJpa itemRepositoryJpa;

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
        Item item = ItemMapper.toItem(getByID(itemId));
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
            item.setId(itemId);
            item.setOwner(userId);
            return ItemMapper.toItemDto(itemRepositoryJpa.save(item));
            } else {
                throw  new WrongParameterException("Вещь не найдена");
           }
    }

    @Override
    public ItemDto getByID(Long id) {
        Item item;
        Optional<Item> optionalItem = itemRepositoryJpa.findById(id);
        if(optionalItem.isPresent()){
            item = optionalItem.get();
        }
        else {
            throw new NotFoundException("Нет вещи с id =" + id);
        }
        return ItemMapper.toItemDto(item);
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

    private void checkUser(Long userId) {
        try {
            userService.getById(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Юзер с ID " + userId + " не найден " + e.getLocalizedMessage());
        }
    }
}
