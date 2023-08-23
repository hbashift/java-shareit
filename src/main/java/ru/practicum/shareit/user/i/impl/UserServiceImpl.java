package ru.practicum.shareit.user.i.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.i.api.UserRepository;
import ru.practicum.shareit.user.i.api.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public User saveUser(User user) {
        log.info("Service layer: create user with email: '{}'.", user.getEmail());

        return repository.saveUser(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Service layer: update user with id: '{}'.", id);

        return repository.updateUser(id, user);
    }

    @Override
    public User getUserById(Long id) {
        log.info("Service layer: get user by id: '{}'.", id);

        return repository.getUserById(id);
    }

    @Override
    public User deleteUserById(Long id) {
        log.info("Service layer: delete user by id: '{}'.", id);

        return repository.deleteUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Service layer: get all users.");

        return repository.getAllUsers();
    }
}