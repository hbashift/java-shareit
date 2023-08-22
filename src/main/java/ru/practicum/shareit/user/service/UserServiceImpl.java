package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.api.UserRepository;
import ru.practicum.shareit.user.service.api.UserService;

import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserDto addUser(UserDto newUserDto) {
        User newUser = userMapper.toUser(newUserDto);
        User addedUser = userRepository.save(newUser);
        log.info("userService: was add user={}", addedUser);
        return userMapper.toUserDto(addedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long userId) {
        User user = findUserById(userId);
        log.info("userService: was returned user={}, by id={}", user, userId);
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = findUserById(userId);
        User newUser = userMapper.toUser(userDto);
        newUser.setId(userId);

        if (Objects.isNull(newUser.getName())) {
            newUser.setName(oldUser.getName());
        }

        if (Objects.isNull(newUser.getEmail())) {
            newUser.setEmail(oldUser.getEmail());
        }

        User updatedUser = userRepository.save(newUser);
        log.info("userService: old user={} update to new user={}", oldUser, updatedUser);

        return userMapper.toUserDto(updatedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        log.info("userService: returned all {} users", allUsers.size());
        return userMapper.map(allUsers);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        log.info("userService: delete user with id={}", id);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id=%d not found", userId)));
    }
}
