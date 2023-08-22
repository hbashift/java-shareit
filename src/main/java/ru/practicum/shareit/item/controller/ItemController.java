package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.i.api.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        log.info("Controller layer: request for item creation obtained.");

        return itemDtoMapper.convertItemToItemDto(itemService.saveItem(userId, itemDtoMapper.itemDtoToItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestHeader("X-Sharer-User-Id") @NotNull  Long userId,
                              @PathVariable final Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Controller layer: request for item with id: '{}' update obtained.", itemId);

        return itemDtoMapper.convertItemToItemDto(itemService.updateItem(userId, itemId,
                itemDtoMapper.itemDtoToItem(itemDto)));
    }

    @GetMapping("/{itemId}")
    public Optional<ItemDto> getItemById(@PathVariable final Long itemId) {
        log.info("Controller layer: request for getting of item with id: '{}' obtained.", itemId);

        return itemDtoMapper.itemToItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public Optional<List<ItemDto>> getAllItemsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Controller layer: request for getting of all items of owner with id: '{}' obtained.", userId);

        return itemDtoMapper.itemsToDtos(itemService.getAllItemsOfOwner(userId));
    }

    @GetMapping("/search")
    public Optional<List<ItemDto>> getItemsByNameOrDescription(@RequestParam String text) {
        log.info("Controller layer: request for search items by name or description obtained.");

        return itemDtoMapper.itemsToDtos(itemService.getItemsByNameOrDescription(text));
    }
}