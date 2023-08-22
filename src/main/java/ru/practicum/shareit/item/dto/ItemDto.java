package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class ItemDto {
    private Long id;

    @NotNull(message = "Parameter 'name' of item shouldn't be null.")
    @NotEmpty(message = "Parameter 'name' of item shouldn't be an empty.")
    private String name;

    @NotNull(message = "Parameter 'description' of item shouldn't be null.")
    private String description;

    @NotNull(message = "Parameter 'available' of item shouldn't be null.")
    private Boolean available;
}