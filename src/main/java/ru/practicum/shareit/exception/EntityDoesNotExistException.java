package ru.practicum.shareit.exception;

public class EntityDoesNotExistException extends RuntimeException {
    public EntityDoesNotExistException(String s) {
        super(s);
    }
}