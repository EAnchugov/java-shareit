package ru.practicum.shareit.items.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.items.ItemClient;
import ru.practicum.shareit.items.dto.ItemDto;

import javax.validation.Valid;

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
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(required = false) Integer from,
                                      @RequestParam(required = false) Integer size) {
//        checkId(userId);
//            return itemClient.getItems(userId, from, size);
        checkId(userId);
        if (from == null || size == null) {
            return itemClient.getItems(userId);
        } else {
            return itemClient.getItems(userId, from, size);
        }
    }

    @GetMapping("/search")
    private  ResponseEntity<Object> search(@RequestParam String text) {
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody CommentClentDto commentClentDto) {
        checkId(itemId);
        checkId(userId);
        return itemClient.createComment(itemId, userId, commentClentDto);
    }

    private void checkId(Long id) {
        if (id < 1) {
            throw new IllegalArgumentException("id не может быть меньше 1");
            }

    }
}
