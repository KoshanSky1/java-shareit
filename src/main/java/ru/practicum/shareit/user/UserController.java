package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("---START CREATE USER ENDPOINT---");
        return new ResponseEntity<>(userService.createUser(toUser(userDto)), HttpStatus.OK);
    }

    @PatchMapping("/{idUser}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long idUser, @RequestBody UserDto userDto) {
        log.info("---START UPDATE USER ENDPOINT---");
        return new ResponseEntity<>(toUserDto(userService.updateUser(idUser, toUser(userDto))), HttpStatus.OK);
    }

    @DeleteMapping("/{idUser}")
    public void deleteUser(@PathVariable int idUser) {
        log.info("---START DELETE USER ENDPOINT---");
        userService.deleteUser(idUser);
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserDto> findUser(@PathVariable long idUser) {
        log.info("---START FIND USER ENDPOINT---");
        return new ResponseEntity<>(toUserDto(userService.getUser(idUser)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAllUsers() {
        log.info("---START FIND ALL USERS ENDPOINT---");
        Collection<User> users = userService.getAllUsers();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            UserDto userDto = toUserDto(user);
            usersDto.add(userDto);
        }
        return new ResponseEntity<>(usersDto, HttpStatus.OK);
    }

}