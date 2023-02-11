package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestAuthorTest {

    @Test
    void all() {
        RequestAuthor requestAuthor = RequestAuthor.builder().build();
        assertEquals(requestAuthor, new RequestAuthor(null,null));

    }
}