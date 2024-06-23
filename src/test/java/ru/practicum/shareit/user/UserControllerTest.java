package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserMapper userMapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    private final User userNumberOne = new User(null, "Linar", "Linar@xakep.ru");


    @Test
    void createUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userNumberOne);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userNumberOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(userNumberOne);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userNumberOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void findAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(userNumberOne);

        when(userService.getAllUsers())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(users))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID, 1))
                .andExpect(status().isOk());

    }
}