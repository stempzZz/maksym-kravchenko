package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.exception.BadRequestException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.model.errors.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException: exception {}", ex.getMessage(), ex);
        return ex.getBindingResult().getAllErrors().stream()
                .map(err -> new Error(err.getDefaultMessage(), LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleBadRequestException(Exception ex) {
        log.error("handleBadRequestException: exception {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFoundException(Exception ex) {
        log.error("handleNotFoundException: exception {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RestrictionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Error handleRestrictionException(Exception ex) {
        log.error("handleRestrictionException: exception {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleException(Exception ex) {
        log.error("handleException: exception {}", ex.getMessage(), ex);
        return new Error(ex.getMessage(), LocalDateTime.now());
    }

}
