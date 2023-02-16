package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.itemDto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestAuthor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDtoOut requestToOutDto(Request request) {
        List<Item> itemsList = new ArrayList<>();
        if (request.getItems() != null) {
            itemsList = request.getItems();
        }
        RequestAuthor requestAuthor = new RequestAuthor(request.getRequester().getId(), request.getRequester().getName());
        return RequestDtoOut.builder()
                .description(request.getDescriptionRequest())
                .id(request.getId())
                .created(request.getCreated())
                .items(itemsList.stream().map(ItemMapper:: toItemDto).collect(Collectors.toList()))
                .requestAuthor(requestAuthor)
                .build();
    }
}
