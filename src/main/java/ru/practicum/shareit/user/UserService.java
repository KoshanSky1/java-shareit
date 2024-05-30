package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    User updateUser(long userId, User user);

    void deleteUser(long idUser);

    User getUser(long idUser);

    Collection<User> getAllUsers();
}