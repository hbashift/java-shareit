package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserDao userDao;

    public User addUser(User user) {
        return userDao.addUser(user);
    }

    public User getUser(Long userId) {
        return userDao.getUser(userId);
    }

    public User updateUser(User user, Long userId) {
        return userDao.updateUser(user, userId);
    }


    public void deleteUser(Long userId) {
        userDao.deleteUser(userId);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }
}
