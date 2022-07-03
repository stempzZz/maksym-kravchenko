package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityInRequestDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepo requestRepo;
    private final ActivityRepo activityRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public RequestDto createRequestForAdd(ActivityInputDto activityInputDto) {
        if (userRepo.checkIfUserIsAdmin(activityInputDto.getCreatorId()))
            throw new RuntimeException("creator with given id is not a regular user");
        Activity activity = modelMapper.map(activityInputDto, Activity.class);
        Request request = requestRepo.createRequestForAdd(activity);
        User creator = userRepo.getUserById(activity.getCreatorId());
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivity(modelMapper.map(activity, ActivityInRequestDto.class));
        requestDto.setUser(modelMapper.map(creator, UserInRequestDto.class));
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
        User creator = userRepo.getUserById(activity.getCreatorId());
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivity(modelMapper.map(activity, ActivityInRequestDto.class));
        requestDto.setUser(modelMapper.map(creator, UserInRequestDto.class));
        return requestDto;
    }

    @Override
    public RequestDto confirmRequest(int requestId) {
        Request request = requestRepo.confirmRequest(requestId);
        return mapRequestToRequestDto(request);
    }

    @Override
    public RequestDto declineRequest(int requestId) {
        Request request = requestRepo.declineRequest(requestId);
        return mapRequestToRequestDto(request);
    }

    @Override
    public RequestDto getRequest(int requestId) {
        Request request = requestRepo.getRequest(requestId);
        return mapRequestToRequestDto(request);
    }

    @Override
    public List<RequestDto> getRequests() {
        List<Request> requests = requestRepo.getRequests();
        return requests.stream()
                .map(this::mapRequestToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRequest(int requestId) {
        requestRepo.deleteRequest(requestId);
    }

    private RequestDto mapRequestToRequestDto(Request request) {
        Activity activity = activityRepo.getActivityById(request.getActivityId());
        User creator = userRepo.getUserById(activity.getCreatorId());
        RequestDto requestDto = modelMapper.map(request, RequestDto.class);
        requestDto.setActivity(modelMapper.map(activity, ActivityInRequestDto.class));
        requestDto.setUser(modelMapper.map(creator, UserInRequestDto.class));
        return requestDto;
    }
}
