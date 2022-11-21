package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryJPA;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Primary
@Validated
@Transactional(readOnly = true)
public class UserServiceJPA implements UserService {
    private final UserRepositoryJPA userRepositoryJPA;

    @Transactional
    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        for (User user : userRepositoryJPA.findAll()) {
            users.add(UserMapper.toUserDTO(user));
        }
        return users;
    }

    @Transactional
    @Override
    public UserDto getById(Long id) {
        User user;
        Optional<User> opUser = userRepositoryJPA.findById(id);
        if (opUser.isPresent()) {
            user = opUser.get();
        } else {
            throw new NotFoundException("Нет пользователя с id =" + id);
        }
        return UserMapper.toUserDTO(user);
    }

    @Transactional
    @Override
    public UserDto create(UserDto userdto) {
        User user = UserMapper.toUser(userdto);
        return UserMapper.toUserDTO(userRepositoryJPA.save(user));
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
        return UserMapper.toUserDTO(userRepositoryJPA.save(user1));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepositoryJPA.deleteById(id);
    }

    private void userDuplicateEmailCheck(User user) {
        List<User> users = userRepositoryJPA.findAll();
        for (User u : users) {
            if (u.getEmail().equals(user.getEmail())) {
            throw new DuplicateEmailException("Email Duplicate");
            }
        }
    }
}
