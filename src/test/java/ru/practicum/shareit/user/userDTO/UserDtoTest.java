package ru.practicum.shareit.user.userDTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void getId() {
        UserDto userDto = new UserDto(null,null, null);
        userDto.setId(1L);
        assertEquals(1L, userDto.getId());
    }
}