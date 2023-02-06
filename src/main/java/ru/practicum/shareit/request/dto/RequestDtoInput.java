package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.userDTO.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class RequestDtoInput {
    private Long id;
    private Long requestorId;
    @NotBlank(groups = {Create.class})
    @NotNull
    private String description;
    private LocalDateTime created = LocalDateTime.now();

    @Override
    public String toString() {
        return "RequestDtoInput{" +
                "id=" + id +
                ", requestorId=" + requestorId +
                ", description='" + description + '\'' +
                ", created=" + created +
                '}';
    }
}
