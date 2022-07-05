package com.epam.spring.time_tracking.exception;

public class ExistingException extends BadRequestException {

    private static final String DEFAULT_MESSAGE = "Entity already exists!";

    public ExistingException() {
        super(DEFAULT_MESSAGE);
    }

    public ExistingException(String message) {
        super(message);
    }
}
