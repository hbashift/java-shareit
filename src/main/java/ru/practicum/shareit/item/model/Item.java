package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

@Data
@Accessors(chain = true)
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto()
                .setAvailable(item.getAvailable())
                .setId(item.getId())
                .setName(item.getName())
                .setDescription(item.getDescription());
    }
}
