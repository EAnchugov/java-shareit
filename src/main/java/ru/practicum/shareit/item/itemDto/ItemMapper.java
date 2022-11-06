package ru.practicum.shareit.item.itemDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .available(item.getAvailable())
                .description(item.getDescription())
                .name(item.getName())
                .id(item.getId())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .name(itemDto.getName())
                .id(itemDto.getId())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }
}
