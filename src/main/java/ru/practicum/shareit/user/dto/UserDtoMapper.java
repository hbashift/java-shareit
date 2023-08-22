package ru.practicum.shareit.user.dto;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class UserDtoMapper {
    public Optional<UserDto> userToUserDto(User user) {
        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(convertUserToUserDto(user));
        }
    }

    public User userDtoToUser(UserDto userDto) {

        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public Optional<List<UserDto>> usersToDtos(Collection<User> users) {
        if (users == null) {
            return Optional.empty();
        } else {
            return Optional.of(users
                    .stream()
                    .map(this::convertUserToUserDto)
                    .collect(Collectors.toList()));
        }
    }

    public UserDto convertUserToUserDto(User user) {

        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}