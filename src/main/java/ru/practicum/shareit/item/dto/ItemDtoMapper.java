package ru.practicum.shareit.item.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class ItemDtoMapper {
    public static Optional<ItemDto> itemToItemDto(Item item) {
        if (item == null) {
            return Optional.empty();
        } else {
            return Optional.of(convertItemToItemDto(item));
        }
    }

    public static Item itemDtoToItem(ItemDto itemDto) {

        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                0L, 0L);
    }

    public static Optional<List<ItemDto>> itemsToDtos(List<Item> items) {
        if (items == null) {
            return Optional.empty();
        } else {
            return Optional.of(items
                    .stream()
                    .map(ItemDtoMapper::convertItemToItemDto)
                    .collect(Collectors.toList()));
        }
    }

    public static ItemDto convertItemToItemDto(Item item) {

        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }
}