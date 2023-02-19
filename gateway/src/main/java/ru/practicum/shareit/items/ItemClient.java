package ru.practicum.shareit.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.items.controller.CommentClentDto;
import ru.practicum.shareit.items.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, Long userId) {
        return post("",userId,itemDto);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Long userId, Long id) {
        return patch("/" + id, userId, itemDto);
    }

    public ResponseEntity<Object> getByID(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItems(long userId, int from, int size) {
        return get("?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> createComment(Long itemId, Long userId, CommentClentDto commentClentDto) {
        System.out.println("-----------------");
        System.out.println("/" + itemId + "/comment");
        System.out.println(userId);
        System.out.println(commentClentDto);
        System.out.println("-----------------");

        return post("/" + itemId + "/comment", userId,commentClentDto);
    }

    public ResponseEntity<Object> search(String text) {
        return get("/search?text=" + text);
    }
}
