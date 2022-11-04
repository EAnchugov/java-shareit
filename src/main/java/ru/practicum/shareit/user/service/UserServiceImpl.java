package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.WrongParameterException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.ArrayList;
import java.util.List;

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
        userNameCheck(user);
        return UserMapper.toUserDTO(userRepository.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        userDuplicateEmailCheck(user);
        User user1 = userRepository.getById(id);
        if (user.getName() != null) {
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

    private void userNameCheck(User user) {
        if (user.getEmail() == null) {
            throw new WrongParameterException("Name = null!");
        }
    }
}
