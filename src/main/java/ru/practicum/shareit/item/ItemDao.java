package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemDao {
    private final HashMap<Long, Item> itemHashMap = new HashMap<>();

    private Long id = 0L;

    @Autowired
    private final UserDao userDao;

    public Item addItem(Long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName())
                .setAvailable(itemDto.getAvailable())
                .setOwner(userDao.getUser(userId))
                .setDescription(itemDto.getDescription())
                .setId(++id);

        itemHashMap.put(item.getId(), item);
        return item;
    }

    public Item updateItem(ItemDto itemDto, Item item, Long itemId) {

        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());

        itemHashMap.put(itemId, item);
        return item;
    }

    public Item getItem(Long itemId) {
        Item item = itemHashMap.get(itemId);
        if (item == null) throw new NotFoundException();
        return item;
    }

    public List<Item> getItems(Long id) {
        return itemHashMap.values().stream()
                .filter(x -> Objects.equals(x.getOwner().getId(), id))
                .collect(Collectors.toList());
    }

    public List<Item> searchItems(String text) {
        return itemHashMap.values().stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                        x.getName().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
