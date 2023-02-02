package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService{
    private final RequestRepository requestRepository;
    private final UserService userService;


    @Override
    public Request create(Long userId, RequestDtoInput input){
        return requestRepository.save(
                Request.builder()
                        .requester(UserMapper.toUser(userService.getById(userId)))
                        .created(LocalDateTime.now())
                        .descriptionRequest(input.getDescription())
                        .build());
    }

    @Override
    public List<Request> getAllUserRequest(Long userId) {
        User user = UserMapper.toUser(userService.getById(userId));
        List<Request> userRequest = new ArrayList<>();
        userRequest.addAll(requestRepository.findAllByRequesterOrderById(user));
        return userRequest;
    }

    @Override
    public List<Request> getAll(Long userId, Integer from, Integer size) {
                if (from < 0 || size <1){
            throw new IllegalArgumentException("Ошибка в getAll");
        }

        User user = UserMapper.toUser(userService.getById(userId));
        List <Request> notUserRequest = new ArrayList<>();
        notUserRequest.addAll(requestRepository.findAllByRequesterNot(user)
                .stream().limit(size).collect(Collectors.toList()));
        return notUserRequest;
    }

    @Override
    public Request getById(Long userId, Long requestId) {
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isPresent()){
            return optionalRequest.get();
        }
        else {
            throw new IllegalArgumentException("нет реквеста с нужным ID");
        }
    }
}
