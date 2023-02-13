package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.userDTO.Create;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDtoOut addRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Validated(Create.class) RequestDtoInput input
            ) {
        return RequestMapper.requestToOutDto(requestService.create(userId,input));

    }

    @GetMapping
    public List<RequestDtoOut> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return requestService.getAllUserRequest(userId)
                .stream().map(RequestMapper::requestToOutDto).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<RequestDtoOut> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @RequestParam(value = "size", defaultValue = "20") Integer size) {
            return requestService.getAll(userId, from,size)
                    .stream().map(RequestMapper::requestToOutDto).collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public  RequestDtoOut getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("requestId") Long requestId) {
        Request request = requestService.getById(userId, requestId);
        return RequestMapper.requestToOutDto(request);
    }
}
