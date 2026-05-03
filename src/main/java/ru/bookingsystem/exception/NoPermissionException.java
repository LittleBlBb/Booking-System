package ru.bookingsystem.exception;

import org.springframework.http.HttpStatus;

public class NoPermissionException extends BusinessException {
    public NoPermissionException(){
        super("you do not have permission to perform this action", HttpStatus.FORBIDDEN.value());
    }

    public NoPermissionException(String message){
        super(message, HttpStatus.FORBIDDEN.value());
    }
}
