package ru.practicum.shareit.user.userDTO;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotNull
    @Email
    private String email;
}