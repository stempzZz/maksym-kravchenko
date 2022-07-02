package com.epam.spring.time_tracking.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInputDto {
    private String lastName;
    private String firstName;
    private String email;
    private String currentPassword;
    private String password;
    private String repeatPassword;
    private boolean isAdmin;
}
