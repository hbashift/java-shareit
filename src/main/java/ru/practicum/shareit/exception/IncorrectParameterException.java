package ru.practicum.shareit.exception;

public class IncorrectParameterException extends RuntimeException {
    public IncorrectParameterException(String s) {
        super(s);
    }
}