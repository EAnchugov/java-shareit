package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Slf4j
@Builder
public class RequestDtoOut {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<Item> items;

    @Data
    @NoArgsConstructor
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;
    }
}
