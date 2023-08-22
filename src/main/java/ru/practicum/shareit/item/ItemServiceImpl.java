package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.CommentException;
import ru.practicum.shareit.exceptions.DeniedAccessException;
import ru.practicum.shareit.exceptions.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.DetailedCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    public static final int MIN_SEARCH_REQUEST_LENGTH = 3;
    public static final String OWNER_NOT_FOUND_MESSAGE = "Не найден владелец c id: ";
    public static final String EMPTY_COMMENT_MESSAGE = "Комментарий не может быть пустым";
    public static final String DENIED_ACCESS_MESSAGE = "Пользователь не является владельцем вещи";
    public static final String COMMENT_EXCEPTION_MESSAGE = "Нельзя оставить комментарий на вещь, " +
            "который вы не пользовались или ещё не закончился срок аренды";

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        boolean ownerExists = isOwnerExists(item.getOwner());
        if (!ownerExists) {
            throw new OwnerNotFoundException(OWNER_NOT_FOUND_MESSAGE + item.getOwner());
        }
        item = itemRepository.save(item);
        return ItemMapper.toDto(item, null);
    }

    @Override
    public DetailedCommentDto createComment(CreateCommentDto dto, Long itemId, Long userId) {
        if (dto.getText().isBlank()) throw new CommentException(EMPTY_COMMENT_MESSAGE);
        Item item = itemRepository.findById(itemId).orElseThrow();
        User author = userRepository.findById(userId).orElseThrow();

        if (bookingRepository.findBookingsForAddComments(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new CommentException(COMMENT_EXCEPTION_MESSAGE + " itemId: " + itemId);
        }
        Comment comment = CommentMapper.toModel(dto, item, author);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDetailedDto(comment);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        item.setId(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        item = itemRepository.save(refreshItem(item));
        return ItemMapper.toDto(item, comments);
    }

    @Override
    public ItemDto findItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<Comment> comments = commentRepository.findByItemId(itemId);

        if (item.getOwner().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            Sort sortDesc = Sort.by("start").descending();
            return constructItemDtoForOwner(item, now, sortDesc, comments);
        }
        return ItemMapper.toDto(item, null, null, comments);
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        List<Item> userItems = itemRepository.findAll()
                .stream().filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());

        List<ItemDto> result = new ArrayList<>();
        fillItemDtoList(result, userItems, userId);

        result.sort((o1, o2) -> {
            if (o1.getNextBooking() == null && o2.getNextBooking() == null) {
                return 0;
            }
            if (o1.getNextBooking() != null && o2.getNextBooking() == null) {
                return -1;
            }
            if (o1.getNextBooking() == null && o2.getNextBooking() != null) {
                return 1;
            }
            if (o1.getNextBooking().getStart().isBefore(o2.getNextBooking().getStart())) {
                return -1;
            }
            if (o1.getNextBooking().getStart().isAfter(o2.getNextBooking().getStart())) {
                return 1;
            }
            return 0;
        });
        return result;
    }

    @Override
    public List<ItemDto> findItemsByRequest(String text, Long userId) {
        if (text == null || text.isBlank() || text.length() <= MIN_SEARCH_REQUEST_LENGTH) {
            return new ArrayList<>();
        }
        List<ItemDto> result = new ArrayList<>();
        List<Item> foundItems = itemRepository.search(text);
        fillItemDtoList(result, foundItems, userId);
        return result;
    }

    private boolean isOwnerExists(long ownerId) {
        List<User> users = userRepository.findAll();
        List<User> result = users.stream().filter(user -> user.getId() == ownerId).collect(Collectors.toList());
        return result.size() > 0;
    }

    private Item refreshItem(Item patch) {
        Item entry = itemRepository.findById(patch.getId()).orElseThrow();

        if (!entry.getOwner().equals(patch.getOwner())) {
            throw new DeniedAccessException(DENIED_ACCESS_MESSAGE +
                    "userId: " + patch.getOwner() + ", itemId: " + patch.getId());
        }

        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String description = patch.getDescription();
        if (description != null && !description.isBlank()) {
            entry.setDescription(description);
        }

        Boolean available = patch.getAvailable();
        if (available != null) {
            entry.setAvailable(available);
        }
        return entry;
    }

    private void fillItemDtoList(List<ItemDto> targetList, List<Item> foundItems, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        Sort sortDesc = Sort.by("start").descending();

        for (Item item : foundItems) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            if (item.getOwner().equals(userId)) {
                ItemDto dto = constructItemDtoForOwner(item, now, sortDesc, comments);
                targetList.add(dto);
            } else {
                targetList.add(ItemMapper.toDto(item, comments));
            }
        }
    }

    private ItemDto constructItemDtoForOwner(Item item, LocalDateTime now, Sort sort, List<Comment> comments) {
        Booking lastBooking = bookingRepository.findBookingByItemIdAndEndBefore(item.getId(), now, sort)
                .stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository.findBookingByItemIdAndStartAfter(item.getId(), now, sort)
                .stream().findFirst().orElse(null);

        return ItemMapper.toDto(item,
                lastBooking,
                nextBooking,
                comments);
    }
}