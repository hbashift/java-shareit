package ru.practicum.shareit.item.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.i.api.ItemRepository;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public Item saveItem(Long userId, Item item) {
        log.info("Service layer: create item with name: '{}'.", item.getName());

        return repository.saveItem(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        log.info("Service layer: update item with id: '{}'.", itemId);

        return repository.updateItem(userId, itemId, item);
    }

    @Override
    public Item getItemById(Long id) {
        log.info("Service layer: get item by id: '{}'.", id);

        return repository.getItemById(id);
    }

    @Override
    public List<Item> getAllItemsOfOwner(Long userId) {
        log.info("Service layer: get all items of owner with id: '{}'.", userId);

        return repository.getAllItemsOfOwner(userId);
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text) {
        log.info("Service layer: get items by name or description.");

        return repository.getItemsByNameOrDescription(text);
    }
}