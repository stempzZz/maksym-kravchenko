package com.epam.spring.time_tracking.dto.user;

import lombok.Data;

@Data
public class UserEditPasswordInputDto {
    private String currentPassword;
    private String password;
    private String repeatPassword;
}
