package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/request/activity/add")
    public RequestDto createRequestForAdd(@RequestBody ActivityInputDto activityInputDto) {
        return requestService.createRequestForAdd(activityInputDto);
    }

    @PostMapping("/request/activity/{activityId}/remove")
    public RequestDto createRequestForRemove(@PathVariable int activityId) {
        return requestService.createRequestForRemove(activityId);
    }

    @PostMapping("/request/{requestId}/confirm")
    public RequestDto confirmRequest(@PathVariable int requestId) {
        return requestService.confirmRequest(requestId);
    }

    @PostMapping("/request/{requestId}/decline")
    public RequestDto declineRequest(@PathVariable int requestId) {
        return requestService.declineRequest(requestId);
    }

    @GetMapping("/request/{requestId}")
    public RequestDto getRequest(@PathVariable int requestId) {
        return requestService.getRequest(requestId);
    }

    @GetMapping("/request")
    public List<RequestDto> getRequests() {
        return requestService.getRequests();
    }

    @DeleteMapping("/request/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable int requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}
