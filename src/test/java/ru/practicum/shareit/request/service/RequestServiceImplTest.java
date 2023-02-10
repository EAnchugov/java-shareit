package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDtoInput;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "db.name=RequestServiceImplTest",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {
    private final RequestService requestService;
    private final UserService userService;
    private RequestDtoInput requestDtoInput;
    private RequestDtoInput requestDtoInput2;
    private RequestDtoOut requestDtoOut;
    User user;
    User user2;
    UserDto userDto;
    private UserDto userDto2;
    Request request;
    //

    @BeforeEach
    void setUp() {
        requestDtoInput = RequestDtoInput.builder().description("Запрос").build();
        requestDtoInput2 = requestDtoInput;
        user = new User(1L,"name", "mail@mail.org");
        user2 = new User(2L,"name2", "mail2@mail.org");
        userDto = UserMapper.toUserDTO(user);
        userDto2 = UserMapper.toUserDTO(user2);

        userDto = userService.create(UserMapper.toUserDTO(user));
        request = requestService.create(userDto.getId(), requestDtoInput);
        userDto2 = userService.create(UserMapper.toUserDTO(user2));
        requestService.create(userDto2.getId(), requestDtoInput);
    }

    @Test
    void create() {
        assertEquals(request.getDescriptionRequest(),requestDtoInput.getDescription());
        assertThat(request.getCreated(), notNullValue());
        assertThat(request.getId(), notNullValue());
    }

    @Test
    void getAllUserRequest() {
//        userDto = userService.create(UserMapper.toUserDTO(user));
//        userDto2 = userService.create(UserMapper.toUserDTO(user2));
//        requestService.create(userDto.getId(), requestDtoInput);
        List<Request> requests = requestService.getAllUserRequest(userDto.getId());
        assertEquals(requests.size(), 1);
    }

    @Test
    void getAll() {
//        userDto = userService.create(UserMapper.toUserDTO(user));
//        userDto2 = userService.create(UserMapper.toUserDTO(user2));
//        requestService.create(userDto.getId(), requestDtoInput);
//        requestService.create(userDto2.getId(), requestDtoInput);
        List<Request> requests = requestService.getAll(userDto.getId(), 0,1);
        assertEquals(requests.size(), 1);
    }

    @Test
    void getById() {
//        userDto = userService.create(UserMapper.toUserDTO(user));
//        userDto2 = userService.create(UserMapper.toUserDTO(user2));
//        Request request = requestService.create(userDto.getId(), requestDtoInput);
//        Request request = requestService.getById(userDto.getId(), 1L);
        assertEquals(7L, request.getId());
    }
}