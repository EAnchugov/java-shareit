package ru.practicum.shareit.item.itemDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
                .id(itemDto.getId())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .name(itemDto.getName())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }
}
