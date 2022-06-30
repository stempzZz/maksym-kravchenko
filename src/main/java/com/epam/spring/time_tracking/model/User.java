package com.epam.spring.time_tracking.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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

    public User() {

    }

    public User(int id, String lastName, String firstName, String email, String password, int activityCount, double spentTime, boolean isAdmin, boolean isBlocked) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.activityCount = activityCount;
        this.spentTime = spentTime;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
    }
}
