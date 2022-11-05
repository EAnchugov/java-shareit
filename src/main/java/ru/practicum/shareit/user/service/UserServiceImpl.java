package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDTO.Create;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.getAll()) {
            users.add(UserMapper.toUserDTO(user));
        }
        return users;
    }

    public UserDto getById(Long id) {
        return UserMapper.toUserDTO(userRepository.getById(id));
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        userDuplicateEmailCheck(user);
        return UserMapper.toUserDTO(userRepository.create(user));
    }

    @Validated(Create.class)
    public UserDto update(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        User user1 = userRepository.getById(id);
        if (!(Objects.equals(user.getEmail(), user1.getEmail()))) {
            userDuplicateEmailCheck(user);
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            user1.setName(user.getName());
        }
        if (user.getEmail() != null) {
            user1.setEmail(user.getEmail());
        }
        return UserMapper.toUserDTO(userRepository.update(user1));
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    private void userDuplicateEmailCheck(User user) {
        List<User> users = userRepository.getAll();
        for (User u : users) {
            if (u.getEmail().equals(user.getEmail())) {
            throw new DuplicateEmailException("Email Duplicate");
            }
        }
    }
}
