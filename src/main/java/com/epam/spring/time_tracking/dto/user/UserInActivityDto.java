package com.epam.spring.time_tracking.dto.user;

import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInActivityDto {

    private Long activityId;
    private UserOnlyNameDto user;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private double spentTime;
    private ActivityUserStatus status;

}
