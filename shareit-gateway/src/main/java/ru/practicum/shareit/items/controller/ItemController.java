package ru.practicum.shareit.items.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.items.ItemClient;
import ru.practicum.shareit.items.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader ("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        checkId(userId);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ItemDto itemDto,
                                         @PathVariable Long id,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        checkId(userId);
        checkId(userId);
        return itemClient.update(itemDto, userId, id);
    }

    @GetMapping("/{id}")
    private ResponseEntity<Object> getById(@PathVariable Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        checkId(userId);
        return itemClient.getByID(id, userId);
    }

    @GetMapping
    private ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        checkId(userId);
        return itemClient.getAllByOwnerId(userId);
    }

    @GetMapping("/search")
    private  ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        checkId(itemId);
        checkId(userId);
        return itemClient.createComment(itemId, userId, commentDto);
    }

    private void checkId(Long id) {
        if (id < 1){throw new IllegalArgumentException("id не может быть меньше 1");}
    }
}
