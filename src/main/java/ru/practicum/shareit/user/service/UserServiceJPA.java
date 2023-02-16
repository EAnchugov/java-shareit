package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDTO.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceJPA implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Нет пользователя с id =" + id));
        return UserMapper.toUserDTO(user);
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
        return UserMapper.toUserDTO(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto update(UserDto userdto, Long id) {
        User updateUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userdto.getName() != null && !userdto.getName().isBlank()) {
            updateUser.setName(userdto.getName());
        }
        if (userdto.getEmail() != null && !(userdto.getEmail().isBlank())) {
            updateUser.setEmail(userdto.getEmail());
        }
        return UserMapper.toUserDTO(updateUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
