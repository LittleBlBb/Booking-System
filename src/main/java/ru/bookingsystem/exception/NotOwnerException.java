package ru.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class NotOwnerException extends BusinessException {
    public NotOwnerException(){
        super("You are not the owner of this company", HttpStatus.FORBIDDEN.value());
    }
}
