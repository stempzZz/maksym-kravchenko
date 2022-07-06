package com.epam.spring.time_tracking.exception;

public class VerificationException extends BadRequestException {

    public VerificationException(String message) {
        super(message);
    }

}
