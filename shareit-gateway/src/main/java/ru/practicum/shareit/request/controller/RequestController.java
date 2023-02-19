package ru.practicum.shareit.request.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.requestDto.RequestDtoInput;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody RequestDtoInput input
    ) {
        return requestClient.create(userId,input);

    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return requestClient.getAllUserRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return requestClient.getAll(userId, from,size);
    }

    @GetMapping("/{requestId}")
    public  ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("requestId") Long requestId) {
        return requestClient.getById(userId, requestId);
    }
}