package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJPATest {
    private static final long USER_ID_1 = 1L;
    private static final String USERNAME_1 = "User_1";
    private static final User USER_1 = User.builder().email("asdf@mail.org").name(USERNAME_1).id(USER_ID_1).build();
    private static final String USERNAME_2 = "User_2";
    private static final User USER_2 = User.builder().email("user2@mail.org").name(USERNAME_2).build();

    private static final long SUCCESS_ID = 2L;
    private static final long BAD_ID = 33L;
    UserServiceJPA userServiceJPA;
    List<User> list = new ArrayList<>();
    @Mock
    UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        userServiceJPA = new UserServiceJPA(userRepository);
        list.add(USER_1);
        list.add(USER_2);
    }

    @Test
    void create() {
        when(userRepository.save(any(User.class))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocationOnMock) throws Throwable {
                User user = invocationOnMock.getArgument(0,User.class);
                user.setId(USER_ID_1);
                return user;
            }
        });
        UserDto testUserDto = userServiceJPA.create(UserMapper.toUserDTO(USER_1));
        assertEquals(USER_ID_1, testUserDto.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(list);
       List<UserDto> testUsers = userServiceJPA.getAll();
       assertEquals(2,testUsers.size());
       assertTrue(testUsers.stream().anyMatch(elem -> elem.getName().equals(USERNAME_1)));
       assertTrue(testUsers.stream().anyMatch(elem -> elem.getName().equals(USERNAME_2)));
    }

    @Test
    void getById_exception() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userServiceJPA.getById(BAD_ID),
                "Ожидался exception а его нет");
        assertTrue(ex.getMessage().contains("Нет пользователя с id"));
    }

    @Test
    void getById() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(USER_1));
        UserDto userDto = userServiceJPA.getById(SUCCESS_ID);
        assertEquals(userDto.getEmail(), USER_1.getEmail());
    }

    @Test
    void update_exception() {
        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                        userServiceJPA.update(UserDto.builder().build(), BAD_ID),
                "Ожидался exception а его нет");
        assertTrue(ex.getMessage().equals("Пользователь не найден"));

    }

    @Test
    void update() {
        when(userRepository.findById(eq(SUCCESS_ID))).thenReturn(Optional.of(USER_1));
        UserDto testUserDTO = userServiceJPA.update(UserMapper.toUserDTO(USER_2), SUCCESS_ID);
        assertEquals(testUserDTO.getName(), USER_2.getName());
        assertEquals(testUserDTO.getEmail(), USER_2.getEmail());
    }

    @Test
    void delete() {
        userServiceJPA.delete(11L);
        verify(userRepository).deleteById(eq(11L));
    }
}