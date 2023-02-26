package ru.practicum.shareit.items.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя пустое или состоит из пробелов")
    private String name;
    @NotBlank(message = "Описание пустое или состоит из пробелов")
    private String description;
    @NotNull(message = "Не заполнена доступность")
    private Boolean available;
    private Long requestId;

}