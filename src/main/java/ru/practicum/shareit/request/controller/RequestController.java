package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.userDTO.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDtoOut addRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Validated(Create.class) RequestDtoInput input
            ){
        Request request = requestService.create(userId,input);

        return RequestDtoOut.builder()
                .descriptionRequest(request.getDescriptionRequest())
                .id(request.getId())
                .requestor(request.getRequester())
                .created(request.getCreated())
                .build();
    }

    @GetMapping
    public List<RequestDtoOut> getAllUserRequest (@RequestHeader("X-Sharer-User-Id") Long userId
    ){
        return requestService.getAllUserRequest(userId)
                .stream().map(RequestMapper :: mapper1).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<RequestDtoOut> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", defaultValue = "20") Integer size) {
        if (userId<1 || size <1){
            throw new IllegalArgumentException("Ошибка в getAll");
        }
        else {
            return requestService.getAll(userId, from,size)
                    .stream().map(RequestMapper :: mapper1).collect(Collectors.toList());
        }

    }

    @GetMapping("/{requestId}")
    public RequestDtoOut getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("requestId") Long requestId) {
        requestService.getById(userId, requestId);
        return RequestMapper.mapper1(requestService.getById(userId, requestId));
    }
}
