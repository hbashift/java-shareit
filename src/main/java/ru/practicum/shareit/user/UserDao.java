package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.DuplicateException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;

import java.util.*;

@Repository
public class UserDao {
    private final HashMap<Long, User> userHashMap = new HashMap<>();

    private Long userId = 0L;

    public User addUser(User user) {
        if (userHashMap.entrySet().stream()
                .anyMatch(x -> Objects.equals(x.getValue().getEmail(), user.getEmail()))
        ) throw new DuplicateException();
        user.setId(++userId);
        userHashMap.put(user.getId(), user);
        return user;
    }

    public User getUser(Long userId) {
        if (userHashMap.containsKey(userId)) {
            return userHashMap.get(userId);
        } else {
            throw new NotFoundException();
        }
    }

    public User updateUser(User user, Long userId) {
        if (!userHashMap.containsKey(userId)) throw new NotFoundException();
        User updateUser = userHashMap.get(userId);

        boolean isDuplicate = userHashMap.entrySet().stream()
                .anyMatch(x -> Objects.equals(x.getValue().getEmail(), user.getEmail()));

        if (isDuplicate &&
                !Objects.equals(updateUser.getEmail(), user.getEmail())) throw new DuplicateException();

        if (user.getName() == null) {
            updateUser.setEmail(user.getEmail());
        } else if (user.getEmail() == null) {
            updateUser.setName(user.getName());
        } else {
            updateUser.setName(user.getName());
            updateUser.setEmail(user.getEmail());
        }

        return updateUser;
    }

    public void deleteUser(Long userId) {
        if (userHashMap.containsKey(userId)) {
            userHashMap.remove(userId);
        } else {
            throw new NotFoundException();
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(userHashMap.values());
    }
}
