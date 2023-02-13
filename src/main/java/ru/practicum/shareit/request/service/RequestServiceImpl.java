package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PaginationCheck;
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

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final PaginationCheck paginationCheck;


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
        return userRequest;
    }

    @Override
    public List<Request> getAll(Long userId, Integer from, Integer size) {
        paginationCheck.paginationCheck(from, size);
        userService.getById(userId);
        List<Request> requests;
        requests = requestRepository
                .getAllWithSize(userId, PageRequest.of(from, size, Sort.by(DESC, "created")));

        requests = addItemsToRequest(requests);
        return requests;
    }

    @Override
    public Request getById(Long userId, Long requestId) {
        userService.getById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Нет такого реквеста"));
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        addItemsToRequest(requests);
        return requests.get(0);
    }

    private List<Request> addItemsToRequest(List<Request> requests) {
        for (Request r: requests) {
            List<Item> items = itemService.getItemsByRequest(r.getId());
            if (r.getItems() == null) {
                r.setItems(new ArrayList<>());
            } else {
                r.setItems(items);
            }
        }
        return requests;
    }
}
