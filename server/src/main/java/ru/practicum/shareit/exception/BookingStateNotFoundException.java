package ru.practicum.shareit.exception;

public class BookingStateNotFoundException extends RuntimeException {

    public BookingStateNotFoundException(final String message) {
        super(message);
    }

}