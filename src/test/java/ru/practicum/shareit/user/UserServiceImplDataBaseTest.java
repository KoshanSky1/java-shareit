package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplDataBaseTest {

    private final UserServiceImpl userService;

    private final User owner = new User(null, "Linar", "Linar@xakep.ru");

    private final User booker = new User(null, "Lenar", "Lenar@xakep.ru");

    @BeforeEach
    void beforeEach() {
        userService.createUser(owner);
        userService.createUser(booker);
    }

    @Test
    void getAllUsers() {
        Collection<User> usersExpected = new ArrayList<>();
        usersExpected.add(owner);
        usersExpected.add(booker);

        Collection<User> usersActual = userService.getAllUsers();

        assertEquals(usersExpected, usersActual);
    }
}