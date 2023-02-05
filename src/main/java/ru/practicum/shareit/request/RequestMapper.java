package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDtoOut requestToOutDto(Request request) {
        return RequestDtoOut.builder()
                .description(request.getDescriptionRequest())
                .id(request.getId())
                .requestor(request.getRequester())
                .created(request.getCreated())
                .items(new ArrayList<>())
                .build();
    }
}
