package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import lombok.Data;

@Data
public class ActivityForUserProfileDto {

    private Long id;
    private String name;
    private double spentTime;
    private ActivityUserStatus status;

}
