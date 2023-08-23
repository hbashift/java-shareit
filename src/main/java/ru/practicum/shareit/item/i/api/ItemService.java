package ru.practicum.shareit.item.i.api;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto saveItem(Long userId, Item item);

    ItemDto updateItem(Long userId, Long itemId, Item item);

    Optional<ItemDto> getItemById(Long id);

    Optional<List<ItemDto>> getAllItemsOfOwner(Long userId);

    Optional<List<ItemDto>> getItemsByNameOrDescription(String text);
}