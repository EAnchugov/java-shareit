package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.user.userDTO.Create;

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

    private String description;
    private LocalDateTime created = LocalDateTime.now();
}
