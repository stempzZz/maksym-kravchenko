package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.model.UserActivity;
import lombok.Data;

@Data
public class ActivityForUserProfileDto {

    private int id;
    private String name;
    private double spentTime;
    private UserActivity.Status status;

}
