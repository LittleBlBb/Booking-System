package ru.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class UserNotActivatedException extends BusinessException {
    public UserNotActivatedException(String message) {
        super(message, HttpStatus.FORBIDDEN.value());
    }
}
