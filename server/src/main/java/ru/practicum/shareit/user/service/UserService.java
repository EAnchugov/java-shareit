package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(Long id);

    UserDto create(UserDto userdto);

    UserDto update(UserDto userdto, Long id);

    void delete(Long id);
}