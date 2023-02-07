package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestAuthor;
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
            throw new IllegalArgumentException("Неверные from или size");
        }
//        from = from / size;
        User user = UserMapper.toUser(userService.getById(userId));
        List<Request> notUserRequest = new ArrayList<>();
//        Pageable page = PageRequest.of(from, size, Sort.by("creationDate").descending());
        notUserRequest = requestRepository
                .getAllWithSize(userId, PageRequest.of(from, size, Sort.by(DESC, "created")));
//        notUserRequest.addAll(requestRepository.findAllByRequesterNot(user)
//                .stream().limit(size).collect(Collectors.toList()));
//        notUserRequest.stream().filter(request -> request.getId() >= from && request.getId() <= from+size);
        notUserRequest = addItemsToRequest(notUserRequest);
        return notUserRequest;
    }

    @Override
    public List<Request> getById(Long userId, Long requestId) {
        userService.getById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Нет такого реквеста"));
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        requests = addItemsToRequest(requests);
        return requests;
    }

    private List<Request> addItemsToRequest (List<Request> requests){
        for (Request r: requests) {
 //           UserDto author = userService.getById(r.getRequester().getId());

            List<Item> items = itemService.getItemsByRequest(r.getId());
            if (r.getItems() == null){
                r.setItems(new ArrayList<>());
 //               r.setRequestAuthor(new RequestAuthor(author.getId(),author.getName()));
            }else {
                r.setItems(items);
            }
        }
        return requests;
    }

    private List<Request> toPageDTO(Page<Request> items) {
        return items.stream()
                .map(i -> i)
                .collect(Collectors.toList());
    }
}
