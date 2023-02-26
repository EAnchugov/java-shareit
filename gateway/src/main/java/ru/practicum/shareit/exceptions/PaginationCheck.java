package ru.practicum.shareit.exceptions;

import org.springframework.stereotype.Service;

@Service
public class PaginationCheck {
    public void paginationCheck(Integer from, Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("From меньше 0");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Size меньше 1");
        }
    }
}
