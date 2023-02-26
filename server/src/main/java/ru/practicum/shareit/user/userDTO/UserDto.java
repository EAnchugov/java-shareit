package ru.practicum.shareit.user.userDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @Email(groups = {Update.class, Create.class})
    @NotNull(groups = {Create.class})
    private String email;
}