package ru.practicum.shareit.item.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.i.api.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.i.api.UserRepository;
import ru.practicum.shareit.validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Validator validator;
    private final UserRepository userRepository;

    @Override
    public Item saveItem(Long userId, Item item) {
        log.info("Repository layer: request to in memory storage for item with name: '{}' creation obtained.",
                item.getName());

        item.setOwnerId(userId);
        Item validatedItem = validator.validateItemInMemory(item, items, userId, userRepository.getAllUsers(),
                true);
        items.put(validatedItem.getId(), validatedItem);

        return getItemById(validatedItem.getId());
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        log.info("Repository layer: request to in memory storage for item with id: '{}' update obtained.", itemId);

        item.setId(itemId);
        Item validatedItem = validator.validateItemInMemory(item, items, userId, userRepository.getAllUsers(),
                false);

        if (item.getName() != null) {
            validatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            validatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            validatedItem.setAvailable(item.getAvailable());
        }

        items.put(validatedItem.getId(), validatedItem);

        return getItemById(validatedItem.getId());
    }

    @Override
    public Item getItemById(Long id) {
        log.info("Repository layer: request to in memory storage for item with id: '{}' getting obtained.", id);

        if (!items.containsKey(id)) {
            String itemWarning = "Item with id: " + id + " doesn't exist.";
            throw new EntityDoesNotExistException(itemWarning);
        }

        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsOfOwner(Long userId) {
        log.info("Repository layer: request to in memory storage for getting of all items of owner with id: '{}' " +
                "obtained.", userId);

        return items.values()
                .stream()
                .filter(p -> p.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByNameOrDescription(String text) {
        log.info("Repository layer: request to in memory storage for getting of items by name or description " +
                "obtained.");

        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return items.values()
                    .stream()
                    .filter(p -> p.getName().toLowerCase().contains(text.toLowerCase()) |
                            p.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(p -> p.getAvailable().equals(true))
                    .collect(Collectors.toList());
        }
    }
}