package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.dto.request.RequestForListDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestService {

    List<RequestForListDto> getRequests(Pageable pageable);

    RequestDto getRequest(Long requestId);

    RequestDto createRequestToAdd(ActivityDto activityDto);

    RequestDto createRequestToRemove(Long activityId);

    RequestDto confirmRequest(Long requestId);

    RequestDto declineRequest(Long requestId);

    void deleteRequest(Long requestId);

}
