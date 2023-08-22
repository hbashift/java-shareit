package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemDao itemDao;

    public ItemDto addItem(Long id, ItemDto itemDto) {
        return Item.toItemDto(itemDao.addItem(id, itemDto));
    }

    public ItemDto updateItem(Long ownerId, ItemDto itemDto, Long itemId) {
        Item item = itemDao.getItem(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId)) throw new NotFoundException();
        return Item.toItemDto(
                itemDao.updateItem(itemDto, item, itemId)
        );
    }

    public ItemDto getItem(Long itemId) {
        return Item.toItemDto(itemDao.getItem(itemId));
    }

    public List<ItemDto> getItems(Long id) {
        return itemDao.getItems(id).stream()
                .map(Item::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) return List.of();
        return itemDao.searchItems(text).stream()
                .map(Item::toItemDto)
                .collect(Collectors.toList());
    }
}
