package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.Dto.CommentDto;
import ru.practicum.shareit.item.itemDto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId, Long itemId);

    ItemDto getByID(Long id, Long userId);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> search(String request);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
