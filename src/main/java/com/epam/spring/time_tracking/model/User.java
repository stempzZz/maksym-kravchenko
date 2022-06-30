package com.epam.spring.time_tracking.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String lastName;
    private String firstName;
    private String email;
    private String password;
    private int activityCount;
    private double spentTime;
    private boolean isAdmin;
    private boolean isBlocked;
}
