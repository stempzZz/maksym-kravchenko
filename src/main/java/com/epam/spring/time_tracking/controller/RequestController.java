package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<RequestDto> getRequests() {
        log.info("Getting requests");
        return requestService.getRequests();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{requestId}")
    public RequestDto getRequest(@PathVariable int requestId) {
        log.info("Getting request with id: {}", requestId);
        return requestService.getRequest(requestId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/activity/add")
    public RequestDto createRequestToAdd(@RequestBody ActivityInputDto activityInputDto) {
        log.info("Creating request to add an activity: {}", activityInputDto);
        return requestService.createRequestToAdd(activityInputDto);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/activity/{activityId}/remove")
    public RequestDto createRequestToRemove(@PathVariable int activityId) {
        log.info("Creating request to remove an activity with id: {}", activityId);
        return requestService.createRequestToRemove(activityId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{requestId}/confirm")
    public RequestDto confirmRequest(@PathVariable int requestId) {
        log.info("Confirmation of request with id: {}", requestId);
        return requestService.confirmRequest(requestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{requestId}/decline")
    public RequestDto declineRequest(@PathVariable int requestId) {
        log.info("Declining of request with id: {}", requestId);
        return requestService.declineRequest(requestId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable int requestId) {
        log.info("Deleting request with id: {}", requestId);
        requestService.deleteRequest(requestId);
        return ResponseEntity.noContent().build();
    }
}
