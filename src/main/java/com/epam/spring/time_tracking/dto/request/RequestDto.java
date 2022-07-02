package com.epam.spring.time_tracking.dto.request;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.model.Request;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestDto {
    private int id;
    private ActivityDto activityDto;
    private Request.Status status;
    private boolean forDelete;
    private LocalDateTime createTime;
}
