package com.epam.spring.time_tracking.dto.request;

import com.epam.spring.time_tracking.dto.activity.ActivityShortInfoDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import com.epam.spring.time_tracking.model.Request;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDto {

    private int id;
    private UserOnlyNameDto user;
    private ActivityShortInfoDto activity;
    private Request.Status status;
    private boolean forDelete;
    private LocalDateTime createTime;

}
