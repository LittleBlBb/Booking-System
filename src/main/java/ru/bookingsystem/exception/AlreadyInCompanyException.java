package ru.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class AlreadyInCompanyException extends BusinessException {
    public AlreadyInCompanyException(String message) {
        super(message, HttpStatus.CONFLICT.value());
    }
}
