package com.epam.spring.time_tracking.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private int id;
    private String lastName;
    private String firstName;
    private boolean isAdmin;
    private boolean isBlocked;

    public UserDto() {

    }

    public UserDto(int id, String lastName, String firstName, boolean isAdmin, boolean isBlocked) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
    }
}
