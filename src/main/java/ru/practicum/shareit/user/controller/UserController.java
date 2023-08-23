package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.i.api.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Controller layer: request for user creation obtained.");

        return userDtoMapper.convertUserToUserDto(userService.saveUser(userDtoMapper.userDtoToUser(userDto)));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable final Long id, @RequestBody UserDto userDto) {
        log.info("Controller layer: request for user with id: '{}' update obtained.", id);

        return userDtoMapper.convertUserToUserDto(userService.updateUser(id, userDtoMapper.userDtoToUser(userDto)));
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@PathVariable final long id) {
        log.info("Controller layer: request for getting user by id: '{}' obtained.", id);

        return userDtoMapper.userToUserDto(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUserById(@PathVariable final Long id) {
        log.info("Controller layer: request for deleting user by id: '{}' obtained.", id);

        return userDtoMapper.convertUserToUserDto(userService.deleteUserById(id));
    }

    @GetMapping
    public Optional<List<UserDto>> getAllUsers() {
        log.info("Controller layer: request for getting all users obtained.");

        return userDtoMapper.usersToDtos(userService.getAllUsers());
    }
}