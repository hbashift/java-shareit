package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    public static final int MIN_ID_VALUE = 1;
    public static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                                @Min(MIN_ID_VALUE)
                                @PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@NotNull(message = NULL_USER_ID_MESSAGE)
                              @Min(MIN_ID_VALUE)
                              @PathVariable Long userId,
                              @Validated({Update.class})
                              @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@NotNull(message = (NULL_USER_ID_MESSAGE))
                               @Min(MIN_ID_VALUE)
                               @PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
