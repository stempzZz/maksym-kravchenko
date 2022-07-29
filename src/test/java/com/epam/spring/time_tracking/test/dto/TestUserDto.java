package com.epam.spring.time_tracking.test.dto;

import com.epam.spring.time_tracking.dto.user.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TestUserDto extends UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String password;

}
