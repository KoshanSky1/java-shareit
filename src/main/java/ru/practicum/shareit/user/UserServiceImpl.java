package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public User createUser(User user) {
        log.info(format("Создан пользователь: %s", user));
        return repository.createUser(user);
    }

    public User updateUser(long userId, User user) {
        log.info(format("Обновлен пользователь id = [%s]", userId));
        return repository.updateUser(userId, user);
    }

    public void deleteUser(long userId) {
        log.info(format("Удалён пользователь id = [%s]", userId));
        repository.deleteUser(userId);
    }

    public User getUser(long userId) {
        log.info(format("Найден пользователь id = [%s]", userId));
        return repository.getUser(userId);
    }

    public Collection<User> getAllUsers() {
        log.info("Сформирован список всех пользователей");
        return repository.getAllUsers();
    }

}