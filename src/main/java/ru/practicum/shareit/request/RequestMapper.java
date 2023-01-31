package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    private static RequestDtoOut itemRequestToOut (Request request){
        return RequestDtoOut.builder()
                .requestor(new User()).build();
    }
    public static RequestDtoOut mapper1(Request request){
        return RequestDtoOut.builder()
                .descriptionRequest(request.getDescriptionRequest())
                .id(request.getId())
                .requestor(request.getRequester())
                .created(request.getCreated())
                .build();
    }
}
