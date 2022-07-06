package com.epam.spring.time_tracking.dto.activity;

import lombok.Data;

@Data
public class ActivityForAdminProfileDto {

    private int id;
    private String name;
    private int peopleCount;

}
