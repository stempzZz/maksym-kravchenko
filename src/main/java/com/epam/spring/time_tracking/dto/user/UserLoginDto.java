package com.epam.spring.time_tracking.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginDto {
    private String email;
    private String password;

    public UserLoginDto() {

    }

    public UserLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
