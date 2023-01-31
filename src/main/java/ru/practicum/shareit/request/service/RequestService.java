package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request create(Long userId, String description);

    Request create(Long userId, RequestDtoInput input);

    List<Request> getAllUserRequest(Long userId);
}
