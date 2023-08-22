package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    public static final int MIN_ID_VALUE = 1;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String NULL_ITEM_ID_MESSAGE = "itemID is null";
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Validated({Create.class})
                              @RequestBody ItemDto itemDto,
                              @NotNull(message = (NULL_ITEM_ID_MESSAGE))
                              @Min(MIN_ID_VALUE)
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public DetailedCommentDto createComment(@Validated({Update.class}) @RequestBody CreateCommentDto commentDto,
                                            @NotNull(message = (NULL_ITEM_ID_MESSAGE))
                                 @Min(MIN_ID_VALUE)
                                 @PathVariable Long itemId,
                                            @NotNull(message = (NULL_USER_ID_MESSAGE))
                                 @Min(MIN_ID_VALUE)
                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated({Update.class})
                              @RequestBody ItemDto itemDto,
                              @NotNull(message = NULL_ITEM_ID_MESSAGE)
                              @Min(MIN_ID_VALUE)
                              @PathVariable Long itemId,
                              @NotNull(message = NULL_USER_ID_MESSAGE)
                              @Min(MIN_ID_VALUE)
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@NotNull(message = NULL_ITEM_ID_MESSAGE)
                                @Min(MIN_ID_VALUE)
                                @PathVariable Long itemId,
                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> findAllItems(@NotNull(message = NULL_USER_ID_MESSAGE)
                                      @Min(MIN_ID_VALUE)
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.findAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByRequest(@RequestParam String text,
                                            @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.findItemsByRequest(text, userId);
    }
}
