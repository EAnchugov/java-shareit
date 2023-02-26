package ru.practicum.shareit.item.itemDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .id(item.getId())
                .owner(new ItemDto.Owner(item.getOwner().getId(),item.getOwner().getName()))
                .requestId(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .name(itemDto.getName())
                .owner(new User(itemDto.getOwner().getId(), itemDto.getOwner().getName(),itemDto.getOwner().getName()))
                .request(itemDto.getRequestId())
                .build();
    }
}
