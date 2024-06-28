package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;

    private User owner = new User(null, "Lena", "Lena@yandex.ru");

    private User booker = new User(null, "Lenar", "Lenar@xakep.ru");

    @Test
    void createUser() {
        owner = userService.createUser(owner);

        Optional<User> user = userService.getUser(owner.getId());

        assertTrue(user.isPresent());
    }

    @Test
    void updateUser() {
        User userUpd = new User(null, "Tema", "Tema@xakep.ru");

        owner = userService.createUser(owner);
        User user = userService.updateUser(owner.getId(), userUpd);

        assertEquals(userUpd.getName(), user.getName());
        assertEquals(userUpd.getEmail(), user.getEmail());
    }

    @Test
    void updateUserWithNull() {
        User userUpd = new User(null, "Tema", null);

        owner = userService.createUser(owner);
        User user = userService.updateUser(owner.getId(), userUpd);

        assertEquals(userUpd.getName(), user.getName());
        assertEquals(owner.getEmail(), user.getEmail());
    }

    @Test
    void deleteUser() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);

        Collection<User> users = userService.getAllUsers();
        userService.deleteUser(owner.getId());

        Collection<User> usersUpdated = userService.getAllUsers();

        assertEquals(users.size() - 1, usersUpdated.size());
    }

    @Test
    void getUser() {
        owner = userService.createUser(owner);

        Optional<User> user = userService.getUser(owner.getId());

        assertTrue(user.isPresent());
    }

    @Test
    void getUserWithId() {

        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(77L),
                "Пользователь с id = [77] не существует"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = [77] не существует"));
    }

    @Test
    void getAllUsers() {
        owner = userService.createUser(owner);
        booker = userService.createUser(booker);

        Collection<User> users = userService.getAllUsers();

        assertEquals(users.size(), users.size());

    }
}