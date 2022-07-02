package com.epam.spring.time_tracking.dto.user;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String lastName;
    private String firstName;
    private int activityCount;
    private double spentTime;
    private boolean isAdmin;
    private boolean isBlocked;
}
