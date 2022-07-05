package com.epam.spring.time_tracking.dto.user;

import com.epam.spring.time_tracking.model.UserActivity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInActivityDto {
    private int activityId;
    private UserOnlyNameDto user;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private double spentTime;
    private UserActivity.Status status;
}
