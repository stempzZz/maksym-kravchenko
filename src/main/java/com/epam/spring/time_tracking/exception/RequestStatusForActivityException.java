package com.epam.spring.time_tracking.exception;

public class RequestStatusForActivityException extends BadRequestException {

    private static final String DEFAULT_MESSAGE = "Request is already confirmed or declined!";

    public RequestStatusForActivityException() {
        super(DEFAULT_MESSAGE);
    }

}
