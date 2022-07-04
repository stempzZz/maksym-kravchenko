package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInRequestDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.dto.user.UserInRequestDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepo requestRepo;
    private final ActivityRepo activityRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<RequestDto> getRequests() {
        log.info("Getting requests");
        List<Request> requests = requestRepo.getRequests();
        return requests.stream()
                .map(this::mapRequestToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto getRequest(int requestId) {
        log.info("Getting request with id: {}", requestId);
        Request request = requestRepo.getRequest(requestId);
        return mapRequestToRequestDto(request);
    }

    @Override
    public RequestDto createRequestToAdd(ActivityDto activityDto) {
        log.info("Creating request to add an activity: {}", activityDto);

        if (userRepo.checkIfUserIsAdmin(activityDto.getCreatorId()))
            throw new RuntimeException("creator with given id is not a regular user");

        Activity activity = modelMapper.map(activityDto, Activity.class);
        User creator = userRepo.getUserById(activity.getCreatorId());

        Request request = requestRepo.createRequestToAdd(activity);
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivity(modelMapper.map(activity, ActivityInRequestDto.class));
        requestDto.setUser(modelMapper.map(creator, UserInRequestDto.class));
        return requestDto;
    }

    @Override
    public RequestDto createRequestToRemove(int activityId) {
        log.info("Creating request to remove an activity with id: {}", activityId);

        Activity activity = activityRepo.getActivityById(activityId);
        if (activity == null)
            throw new RuntimeException("activity is not found");
        if (userRepo.checkIfUserIsAdmin(activity.getCreatorId()))
            throw new RuntimeException("activity wasn't created by regular user");
        User creator = userRepo.getUserById(activity.getCreatorId());

        Request request = requestRepo.createRequestToRemove(activity);
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivity(modelMapper.map(activity, ActivityInRequestDto.class));
        requestDto.setUser(modelMapper.map(creator, UserInRequestDto.class));
        return requestDto;
    }

    @Override
    public RequestDto confirmRequest(int requestId) {
        log.info("Confirmation of request with id: {}", requestId);
        Request request = requestRepo.confirmRequest(requestId);
        return mapRequestToRequestDto(request);
    }

    @Override
    public RequestDto declineRequest(int requestId) {
        log.info("Declining of request with id: {}", requestId);
        Request request = requestRepo.declineRequest(requestId);
        return mapRequestToRequestDto(request);
    }

    @Override
    public void deleteRequest(int requestId) {
        log.info("Deleting request with id: {}", requestId);
        requestRepo.deleteRequest(requestId);
    }

    private RequestDto mapRequestToRequestDto(Request request) {
        log.info("Mapping Request to RequestDto");
        Activity activity = activityRepo.getActivityById(request.getActivityId());
        User creator = userRepo.getUserById(activity.getCreatorId());
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivity(modelMapper.map(activity, ActivityInRequestDto.class));
        requestDto.setUser(modelMapper.map(creator, UserInRequestDto.class));
        return requestDto;
    }
}
