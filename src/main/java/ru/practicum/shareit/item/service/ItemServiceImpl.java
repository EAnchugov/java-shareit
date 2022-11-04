package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        checkUser(userId);
        itemDto.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.create(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        checkUser(userId);
        Item item = itemRepository.getById(itemId);
        if (item != null) {
            if (!(item.getOwner().equals(userId))) {
                throw new NotFoundException("Изменять может только владелец");
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            if (itemDto.getName() != null && !(itemDto.getName().isBlank())) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !(itemDto.getDescription().isBlank())) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getOwner() != null) {
                item.setOwner(itemDto.getOwner());
            }
            if (itemDto.getRequest() != null) {
                item.setRequest(itemDto.getRequest());
            }
            itemRepository.update(item);
            return ItemMapper.toItemDto(item);
            } else {
                throw  new WrongParameterException("Вещь не найдена");
           }
    }

    @Override
    public ItemDto getByID(Long id) {
        return ItemMapper.toItemDto(itemRepository.getById(id));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
            List<ItemDto> items = new ArrayList<>();
            for (Item item : itemRepository.getAll()) {
                if (item.getOwner().equals(userId)) {
                    items.add(ItemMapper.toItemDto(item));
                }
            }
        return items;
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        ArrayList<ItemDto> items = new ArrayList<>();
        if (!request.isBlank()) {
            for (Item item :itemRepository.getAll()) {
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
            throw new NotFoundException("Юзер с ID " + userId + " не найден");
        }
    }

}
