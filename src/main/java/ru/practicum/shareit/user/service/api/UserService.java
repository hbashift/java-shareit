package ru.practicum.shareit.user.service.api;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto newUserDto);

    UserDto getUserById(Long id);

    UserDto updateUser(long userId, UserDto userDto);

    Collection<UserDto> getAllUsers();

    void deleteUserById(Long id);
}
