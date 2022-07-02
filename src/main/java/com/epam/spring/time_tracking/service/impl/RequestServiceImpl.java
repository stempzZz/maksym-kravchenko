package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepo requestRepo;
    private final ActivityRepo activityRepo;
    private final ModelMapper modelMapper;

    @Override
    public RequestDto createRequestForAdd(ActivityInputDto activityInputDto) {
        Activity activity = modelMapper.map(activityInputDto, Activity.class);
        Request request = requestRepo.createRequestForAdd(activity);
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivityDto(modelMapper.map(activity, ActivityDto.class));
        return requestDto;
    }

    @Override
    public RequestDto createRequestForRemove(int activityId) {
        Activity activity = activityRepo.getActivityById(activityId);
        if (activity == null)
            throw new RuntimeException("activity is not found");
        if (!activity.getStatus().equals(Activity.Status.BY_USER))
            throw new RuntimeException("activity wasn't created by regular user");
        Request request = requestRepo.createRequestForRemove(activity);
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivityDto(modelMapper.map(activity, ActivityDto.class));
        return requestDto;
    }

    @Override
    public RequestDto confirmRequest(int requestId) {
        Request request = requestRepo.confirmRequest(requestId);
        Activity activity = activityRepo.getActivityById(request.getActivityId());
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivityDto(modelMapper.map(activity, ActivityDto.class));
        return requestDto;
    }
}
