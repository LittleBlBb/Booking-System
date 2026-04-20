package ru.bookingsystem.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {

    private final int status;

    protected BusinessException(String message, int status){
        super(message);
        this.status = status;
    }

}
