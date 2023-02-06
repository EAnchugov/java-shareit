package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDtoOut requestToOutDto(Request request) {
        List<Item> itemsList = new ArrayList<>();

        if (request.getItems() != null){
            itemsList = request.getItems();
        }
        return RequestDtoOut.builder()
                .description(request.getDescriptionRequest())
                .id(request.getId())
                .requestor(request.getRequester())
                .created(request.getCreated())
//                .items(request.getItems().stream().map(ItemMapper :: toItemDto).collect(Collectors.toList()))
                .items(itemsList.stream().map(ItemMapper :: toItemDto).collect(Collectors.toList()))
                .build();
    }
}
