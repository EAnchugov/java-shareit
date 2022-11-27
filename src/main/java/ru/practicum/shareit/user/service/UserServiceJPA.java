package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceJPA implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(UserMapper.toUserDTO(user));
        }
        return users;
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id =" + id));
        return UserMapper.toUserDTO(user);
    }

    @Transactional
    @Override
    public UserDto create(UserDto userdto) {
        User user = UserMapper.toUser(userdto);
        return UserMapper.toUserDTO(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(UserDto userdto, Long id) {
        User user = UserMapper.toUser(userdto);
        User user1 = UserMapper.toUser(getById(id));
        if (!(Objects.equals(user.getEmail(), user1.getEmail()))) {
            userDuplicateEmailCheck(user);
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            user1.setName(user.getName());
        }
        if (user.getEmail() != null) {
            user1.setEmail(user.getEmail());
        }
        user1.setId(id);
        return UserMapper.toUserDTO(userRepository.save(user1));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void userDuplicateEmailCheck(User user) {
        if (userRepository.findAllByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email Duplicate");
        }
    }
}
