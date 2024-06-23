package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

class UserServiceImplTest {

    UserService userService = Mockito.mock(UserService.class);

    private final User owner = new User(15L, "Lena", "Lena@yandex.ru");

    private final User booker = new User(1L, "Lenar", "Lenar@xakep.ru");

    @Test
    void createUser() {
        Mockito
                .when(userService.createUser(any()))
                .thenReturn(owner);

        userService.createUser(owner);
        Mockito.verify(userService, Mockito.times(1))
                .createUser(owner);
    }

    @Test
    void updateUser() {
        Mockito
                .when(userService.updateUser(anyInt(), any()))
                .thenReturn(booker);

        userService.updateUser(1L, booker);
        Mockito.verify(userService, Mockito.times(1))
                .updateUser(1L, booker);
    }

    @Test
    void deleteUser() {
        Mockito
                .when(userService.getUser(anyInt()))
                .thenReturn(null);

        userService.deleteUser(1L);
        Mockito.verify(userService, Mockito.times(1))
                .deleteUser(1L);
    }

    @Test
    void getUser() {
        Mockito
                .when(userService.getUser(anyInt()))
                .thenReturn(Optional.of(owner));

        userService.getUser(1L);
        Mockito.verify(userService, Mockito.times(1))
                .getUser(1L);
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(owner);

        Mockito
                .when(userService.getAllUsers())
                .thenReturn(users);

        userService.getAllUsers();
        Mockito.verify(userService, Mockito.times(1))
                .getAllUsers();

    }
}