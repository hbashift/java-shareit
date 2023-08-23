package ru.practicum.shareit.item.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.i.api.ItemRepository;
import ru.practicum.shareit.item.i.api.ItemService;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public ItemDto saveItem(Long userId, Item item) {
        log.info("Service layer: create item with name: '{}'.", item.getName());

        return ItemDtoMapper.convertItemToItemDto(repository.saveItem(userId, item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        log.info("Service layer: update item with id: '{}'.", itemId);

        return ItemDtoMapper.convertItemToItemDto(repository.updateItem(userId, itemId, item));
    }

    @Override
    public Optional<ItemDto> getItemById(Long id) {
        log.info("Service layer: get item by id: '{}'.", id);

        return ItemDtoMapper.itemToItemDto(repository.getItemById(id));
    }

    @Override
    public Optional<List<ItemDto>> getAllItemsOfOwner(Long userId) {
        log.info("Service layer: get all items of owner with id: '{}'.", userId);

        return ItemDtoMapper.itemsToDtos(repository.getAllItemsOfOwner(userId));
    }

    @Override
    public Optional<List<ItemDto>> getItemsByNameOrDescription(String text) {
        log.info("Service layer: get items by name or description.");

        return ItemDtoMapper.itemsToDtos(repository.getItemsByNameOrDescription(text));
    }
}