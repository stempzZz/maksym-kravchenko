package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getRequests();

    RequestDto getRequest(int requestId);

    RequestDto createRequestToAdd(ActivityDto activityDto);

    RequestDto createRequestToRemove(int activityId);

    RequestDto confirmRequest(int requestId);

    RequestDto declineRequest(int requestId);

    void deleteRequest(int requestId);
}
