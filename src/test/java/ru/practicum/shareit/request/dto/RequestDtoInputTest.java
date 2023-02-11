package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestDtoInputTest {

    @Test
    void all() {
        RequestDtoInput requestDtoInput = new RequestDtoInput();
        requestDtoInput.setId(1L);
        requestDtoInput.setRequestorId(1L);
        assertEquals(requestDtoInput.getId(), 1L);
        assertEquals(requestDtoInput.getRequestorId(), 1L);
    }
}