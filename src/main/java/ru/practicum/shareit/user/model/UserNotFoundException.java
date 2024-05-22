package ru.practicum.shareit.user.model;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String message) {
        super(message);
    }

}
