package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request create(Long userId, RequestDtoInput input);

    List<Request> getAllUserRequest(Long userId);

    List<Request> getAll(Long userId, Integer from, Integer size);

    List<Request> getById(Long userId, Long requestId);
}
