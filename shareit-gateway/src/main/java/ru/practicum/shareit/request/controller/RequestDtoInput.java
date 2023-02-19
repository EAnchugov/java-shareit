package ru.practicum.shareit.request.controller;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDtoInput {
    private Long id;
    private Long requestorId;
    @NotBlank(message = "Описание не может быть пустым или состоять только из пробелов")
    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
