package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.Collection;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public User createUser(User user) {
        log.info(format("Создан пользователь: %s", user));
        return repository.save(user);
    }

    public User updateUser(long userId, User user) {
        User userUpdated = repository.getById(userId);
        userUpdated.setName(getStringValueOrDefault(user.getName(), userUpdated.getName()));
        userUpdated.setEmail(getStringValueOrDefault(user.getEmail(), userUpdated.getEmail()));
        log.info(format("Обновлен пользователь id = [%s]", userId));
        return repository.save(userUpdated);
    }

    public void deleteUser(long userId) {
        log.info(format("Удалён пользователь id = [%s]", userId));
        repository.deleteById(userId);
    }

    public Optional<User> getUser(long userId) {
        if (repository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(format("Пользователь с id = [%s] не существует", userId));
        }
        return repository.findById(userId);
    }

    public Collection<User> getAllUsers() {
        log.info("Сформирован список всех пользователей");
        return repository.findAll();
    }

    private static String getStringValueOrDefault(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

}