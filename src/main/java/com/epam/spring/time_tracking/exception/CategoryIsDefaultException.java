package com.epam.spring.time_tracking.exception;

public class CategoryIsDefaultException extends BadRequestException {

    private static final String DEFAULT_MESSAGE = "Default category is not updatable and deletable!";

    public CategoryIsDefaultException() {
        super(DEFAULT_MESSAGE);
    }

}
