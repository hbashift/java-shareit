package ru.practicum.shareit.user.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityDoesNotExistException;
import ru.practicum.shareit.user.i.api.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Validator validator;

    @Override
    public User saveUser(User user) {
        log.info("Repository layer: request to in memory storage for user with email: '{}' creation obtained.",
                user.getEmail());

        User validatedUser = validator.validateUserInMemory(user, users, true);
        users.put(validatedUser.getId(), validatedUser);

        return getUserById(validatedUser.getId());
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Repository layer: request to in memory storage for user with id: '{}' update obtained.", id);

        user.setId(id);
        User validatedUser = validator.validateUserInMemory(user, users, false);

        if (user.getName() != null) {
            validatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            validatedUser.setEmail(user.getEmail());
        }

        validatedUser.setId(id);
        users.put(id, validatedUser);

        return getUserById(validatedUser.getId());
    }

    @Override
    public User getUserById(Long id) {
        log.info("Repository layer: request to in memory storage for user with id: '{}' getting obtained.", id);

        if (!users.containsKey(id)) {
            String userWarning = "User with id: " + id + " doesn't exist.";
            throw new EntityDoesNotExistException(userWarning);
        }

        return users.get(id);
    }

    @Override
    public User deleteUserById(Long id) {
        log.info("Repository layer: request to in memory storage for user with id: '{}' deletion obtained.", id);

        if (!users.containsKey(id)) {
            String userWarning = "User with id: " + id + " doesn't exist.";
            throw new EntityDoesNotExistException(userWarning);
        }

        User user = users.get(id);
        users.remove(id);

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Repository layer: request to in memory storage for getting of all users obtained.");

        return new ArrayList<>(users.values());
    }
}