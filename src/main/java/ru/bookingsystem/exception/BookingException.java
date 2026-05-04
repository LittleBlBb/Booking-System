package ru.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends BusinessException {
    public BookingException(String message){
        super(message, HttpStatus.CONFLICT.value());
    }
}
