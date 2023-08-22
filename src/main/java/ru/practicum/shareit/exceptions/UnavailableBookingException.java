package ru.practicum.shareit.exceptions;

public class UnavailableBookingException extends RuntimeException {
    public UnavailableBookingException(String message) {
        super(message);
    }
}
