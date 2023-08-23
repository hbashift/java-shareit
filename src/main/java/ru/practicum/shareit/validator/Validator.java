package ru.practicum.shareit.validator;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Validator {
    private Long userId = 0L;
    private Long itemId = 0L;

    public User validateUserInMemory(User user, Map<Long, User> users, boolean isCreationMethod) {
        String userWarning;
        User validatedUser = user;
        List<User> usersFound = new ArrayList<>(users.values())
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());

        if (isCreationMethod) {
            if (!users.isEmpty()) {
                if (!usersFound.isEmpty()) {
                    userWarning = "User with email: " + user.getEmail() + " is already existing.";
                    throw new EntityAlreadyExistsException(userWarning);
                }
            }

            validatedUser.setId(++userId);
        } else {
            if (!users.containsKey(user.getId())) {
                userWarning = "User with id: " + user.getId() + " doesn't exist.";
                throw new EntityDoesNotExistException(userWarning);
            } else {
                List<User> anotherUsersWithSameEmailFound = usersFound
                        .stream()
                        .filter(u -> !u.getId().equals(user.getId()))
                        .collect(Collectors.toList());

                if (!anotherUsersWithSameEmailFound.isEmpty()) {
                    userWarning = "Another one or more users with the same email: " + user.getEmail() + " exists.";
                    throw new EntityAlreadyExistsException(userWarning);
                }

                validatedUser = users.get(user.getId());
            }
        }

        return validatedUser;
    }

    public Item validateItemInMemory(Item item, Map<Long, Item> items, Long userId, Collection<User> users,
                                     boolean isCreationMethod) {
        String itemWarning;
        String userWarning;
        Item validatedItem = item;
        Optional<User> user;

        if (isCreationMethod) {
            user = users
                    .stream()
                    .filter(u -> u.getId().equals(item.getOwnerId()))
                    .findFirst();

            if (user.equals(Optional.empty())) {
                userWarning = "User with id: " + item.getOwnerId() + " doesn't exist.";
                throw new EntityDoesNotExistException(userWarning);
            }

            validatedItem.setId(++itemId);
        } else {
            validatedItem = items.get(item.getId());

            if (!userId.equals(validatedItem.getOwnerId())) {
                itemWarning = "User with id: " + userId + " is not an owner of item with id: " +
                        validatedItem.getOwnerId();
                throw new EntityDoesNotExistException(itemWarning);
            }
        }

        return validatedItem;
    }
}