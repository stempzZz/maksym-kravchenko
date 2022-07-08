package com.epam.spring.time_tracking.model.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Error {

    private String message;
    private LocalDateTime timestamp;

}
