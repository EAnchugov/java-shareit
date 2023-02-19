package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.requestDto.RequestDtoInput;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, RequestDtoInput input) {
        return post("", userId, input);
    }

    public ResponseEntity<Object> getAllUserRequest(Long userId) {
        return get("",userId);
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        return get("/all?from="+from+"&size="+size, userId);
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/"+requestId,userId);
    }
}
