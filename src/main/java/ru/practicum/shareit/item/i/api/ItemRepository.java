package ru.practicum.shareit.item.i.api;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item saveItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItemById(Long id);

    List<Item> getAllItemsOfOwner(Long userId);

    List<Item> getItemsByNameOrDescription(String text);
}