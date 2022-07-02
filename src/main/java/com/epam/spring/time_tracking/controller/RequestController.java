package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
