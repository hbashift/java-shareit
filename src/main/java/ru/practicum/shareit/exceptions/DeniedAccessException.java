package ru.practicum.shareit.exceptions;

public class DeniedAccessException extends RuntimeException {
    public DeniedAccessException(String message) {
        super(message);
    }
}
