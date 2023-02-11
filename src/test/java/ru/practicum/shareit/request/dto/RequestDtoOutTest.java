package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.RequestAuthor;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestDtoOutTest {
    @Test
    void all() {
        RequestAuthor requestAuthor = new RequestAuthor(1L,"name");
        RequestDtoOut requestDtoOut = RequestDtoOut.builder().build();
        requestDtoOut.setId(1L);
        requestDtoOut.setRequestAuthor(requestAuthor);
        requestDtoOut.setItems(new ArrayList<>());
        requestDtoOut.setCreated(LocalDateTime.now());
        requestDtoOut.setDescription("asdasd");
        assertEquals(requestDtoOut.getId(), 1L);
        assertEquals(requestDtoOut.getRequestAuthor(), requestAuthor);
        assertEquals(requestDtoOut.getItems().size(),0);
        assertEquals(requestDtoOut.getCreated(), LocalDateTime.now());
        assertEquals(requestDtoOut.getDescription(), "asdasd");
    }

}