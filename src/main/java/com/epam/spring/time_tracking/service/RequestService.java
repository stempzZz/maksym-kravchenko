package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;

public interface RequestService {
    RequestDto createRequestForAdd(ActivityInputDto activityInputDto);

    RequestDto createRequestForRemove(int activityId);

    RequestDto confirmRequest(int requestId);
}
