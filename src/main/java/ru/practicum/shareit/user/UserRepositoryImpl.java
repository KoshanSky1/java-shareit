package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.EmailException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;

import static java.lang.String.format;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User createUser(User user) {
        if (checkEmail(user)) {
            id++;
            user.setId(id);
            users.put(id, user);
            return user;
        } else {
            throw new EmailException(format("Пользователь с таким email [%s] уже существует", user.getEmail()));
        }
    }

    @Override
    public User updateUser(long userId, User user) {
        User updatedUser = users.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            user.setId(userId);
            if (checkEmail(user)) {
                updatedUser.setEmail(user.getEmail());
            } else {
                throw new EmailException(format("Пользователь с таким email [%s] уже существует", user.getEmail()));
            }
        }
        return updatedUser;
    }

    @Override
    public void deleteUser(long idUser) {
        users.remove(idUser);
    }

    @Override
    public User getUser(long idUser) {
        return users.get(idUser);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private boolean checkEmail(User checkUser) {
        for (User user : users.values()) {
            if (user.getEmail().equals(checkUser.getEmail())) {
                return user.getId().equals(checkUser.getId());
            }
        }
        return true;
    }

}