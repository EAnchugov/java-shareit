package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;


    @Override
    public Request create(Long userId, RequestDtoInput input) {
        return requestRepository.save(
                Request.builder()
                        .requester(UserMapper.toUser(userService.getById(userId)))
                        .created(LocalDateTime.now())
                        .descriptionRequest(input.getDescription())
                        .build());
    }

    @Override
    public List<Request> getAllUserRequest(Long userId) {
        UserDto userDto = userService.getById(userId);
        User user = UserMapper.toUser(userDto);
        List<Request> userRequest = new ArrayList<>();
        userRequest.addAll(requestRepository.findAllByRequesterOrderById(user));
        userRequest = addItemsToRequest(userRequest);

//        userRequest.stream().forEach(request -> {request.getItems().addAll(items);});
        return userRequest;
    }

    @Override
    public List<Request> getAll(Long userId, Integer from, Integer size) {
                if (from < 0 || size < 1) {
            throw new IllegalArgumentException("Ошибка в getAll");
        }

        User user = UserMapper.toUser(userService.getById(userId));
        List<Request> notUserRequest = new ArrayList<>();
        notUserRequest.addAll(requestRepository.findAllByRequesterNot(user)
                .stream().limit(size).collect(Collectors.toList()));
        notUserRequest.stream().filter(request -> request.getId() >= from && request.getId() <= from+size);
        notUserRequest = addItemsToRequest(notUserRequest);
        return notUserRequest;
    }

    @Override
    public Request getById(Long userId, Long requestId) {
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            return optionalRequest.get();
        } else {
            throw new IllegalArgumentException("нет реквеста с нужным ID");
        }
    }

    @Override
    public List<Request> getAll1(Long userId, Integer from, Integer size) {
        return null;
    }

//    @Override
//    public List<Request> getAll1(Long userId, Integer from, Integer size) {
//        userService.getById(userId);
//        return getWithItems(requestRepository
//                .getAllWithSize(userId, PageRequest.of(from, size, Sort.by(DESC, "created"))));
//    }

    private List<Request> addItemsToRequest (List<Request> requests){

        for (Request r: requests) {
            List<Item> items = itemService.getItemsByRequest(r.getId());
            if (r.getItems() == null){

                r.setItems(new ArrayList<>());
            }else {
                r.setItems(items);
            }
        }
        return requests;

    }
}
