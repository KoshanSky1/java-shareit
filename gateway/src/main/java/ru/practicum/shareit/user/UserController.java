package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ShortUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid ShortUserDto shortUserDto) {
        log.info("---START CREATE USER ENDPOINT---");
        return userClient.createUser(shortUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody @Valid UserDto userDto) {
        log.info("---START UPDATE USER ENDPOINT---");
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("---START DELETE USER ENDPOINT---");
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("---START GET USER BY ID ENDPOINT---");
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser() {
        log.info("---START FIND ALL USERS ENDPOINT---");
        return userClient.getAllUsers();
    }


}
