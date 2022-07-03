package com.epam.spring.time_tracking.dto.user;

import lombok.Data;

@Data
public class UserInRequestDto {
    private int id;
    private String lastName;
    private String firstName;
}
