package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.api.RequestApi;
import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController implements RequestApi {

    private final RequestService requestService;

    @Override
    public List<RequestDto> getRequests() {
        log.info("Getting requests");
        return requestService.getRequests();
    }

    @Override
    public RequestDto getRequest(int requestId) {
        log.info("Getting request with id: {}", requestId);
        return requestService.getRequest(requestId);
    }

    @Override
    public RequestDto createRequestToAdd(ActivityDto activityDto) {
        log.info("Creating request to add an activity: {}", activityDto);
        return requestService.createRequestToAdd(activityDto);
    }

    @Override
    public RequestDto createRequestToRemove(int activityId) {
        log.info("Creating request to remove an activity with id: {}", activityId);
        return requestService.createRequestToRemove(activityId);
    }

    @Override
    public RequestDto confirmRequest(int requestId) {
        log.info("Confirmation of request with id: {}", requestId);
        return requestService.confirmRequest(requestId);
    }

    @Override
    public RequestDto declineRequest(int requestId) {
        log.info("Declining of request with id: {}", requestId);
        return requestService.declineRequest(requestId);
    }

    @Override
    public ResponseEntity<Void> deleteRequest(int requestId) {
        log.info("Deleting request with id: {}", requestId);
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }

}
