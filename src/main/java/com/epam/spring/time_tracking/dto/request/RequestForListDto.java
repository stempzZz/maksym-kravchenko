package com.epam.spring.time_tracking.dto.request;

import com.epam.spring.time_tracking.dto.activity.ActivityOnlyNameDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import com.epam.spring.time_tracking.model.enums.status.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestForListDto {

    private Long id;
    private UserOnlyNameDto user;
    private ActivityOnlyNameDto activity;
    private RequestStatus status;
    private boolean forDelete;
    private LocalDateTime createTime;

}
