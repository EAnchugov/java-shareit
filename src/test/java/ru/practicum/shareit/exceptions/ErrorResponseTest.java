package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void getError() {
        ErrorResponse errorResponse = new ErrorResponse("error");
        assertEquals(errorResponse.getError(),"error");
    }
}