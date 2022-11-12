package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.itemDto.ItemDto;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceJPA;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(ItemDto dto, Long userId) {
        checkUser(userId);
        dto.setOwner(userId);
        return ItemMapper.toItemDto(itemRepository.create(ItemMapper.toItem(dto)));
    }

    @Override
    public ItemDto update(ItemDto dto, Long userId, Long itemId) {
        checkUser(userId);
        Item item = itemRepository.getById(itemId);
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
    public List<ItemDto> getAll(Long userId) {
            List<ItemDto> items = new ArrayList<>();
            for (Item item : itemRepository.getAll()) {
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
            throw new NotFoundException("Юзер с ID " + userId + " не найден " + e.getLocalizedMessage());
        }
    }

}
