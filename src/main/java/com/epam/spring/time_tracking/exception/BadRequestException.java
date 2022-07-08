package com.epam.spring.time_tracking.exception;

public abstract class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

}
