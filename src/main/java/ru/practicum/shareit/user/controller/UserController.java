package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceJPA;
import ru.practicum.shareit.user.userDTO.Create;
import ru.practicum.shareit.user.userDTO.Update;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
  //  private final UserService userService;
    private final UserServiceJPA userServiceJPA;

    @GetMapping
    public List<UserDto> getAll() {
        return userServiceJPA.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userServiceJPA.getById(id);
    }

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userServiceJPA.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Validated(Update.class)@RequestBody UserDto userDto, @PathVariable Long id) {
        return userServiceJPA.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userServiceJPA.delete(id);
    }
}
