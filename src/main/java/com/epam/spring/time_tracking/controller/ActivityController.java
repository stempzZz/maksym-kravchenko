package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ActivityDto> getActivities() {
        log.info("Getting all available activities");
        return activityService.getActivities();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}")
    public ActivityDto getActivity(@PathVariable int activityId) {
        log.info("Getting activity with id: {}", activityId);
        return activityService.getActivity(activityId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/user/{userId}")
    public List<ActivityDto> getActivitiesForUser(@PathVariable int userId) {
        log.info("Getting activities for regular user with id: {}", userId);
        return activityService.getActivitiesForUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ActivityDto createActivity(@RequestBody @Valid ActivityDto activityDto) {
        log.info("Creating activity: {}", activityDto);
        return activityService.createActivity(activityDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}/user")
    public List<UserInActivityDto> getActivityUsers(@PathVariable int activityId) {
        log.info("Getting users for activity with id: {}", activityId);
        return activityService.getActivityUsers(activityId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}/user/available")
    public List<UserDto> getUsersNotInActivity(@PathVariable int activityId) {
        log.info("Getting users who are not in activity with id: {}", activityId);
        return activityService.getUsersNotInActivity(activityId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{activityId}/user/{userId}")
    public UserInActivityDto getUserInActivity(@PathVariable int activityId, @PathVariable int userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);
        return activityService.getUserInActivity(activityId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{activityId}/user/{userId}")
    public UserInActivityDto addUserToActivity(@PathVariable int activityId, @PathVariable int userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);
        return activityService.addUserToActivity(activityId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{activityId}/user/{userId}")
    public ResponseEntity<Void> removeUserFromActivity(@PathVariable int activityId, @PathVariable int userId) {
        log.info("Removing user (id={}) from an activity with id: {}", userId, activityId);
        activityService.removeUserFromActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{activityId}/user/{userId}/start")
    public UserInActivityDto startActivity(@PathVariable int activityId, @PathVariable int userId) {
        log.info("User (id={}) starts activity with id: {}", userId, activityId);
        return activityService.startActivity(activityId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{activityId}/user/{userId}/stop")
    public UserInActivityDto stopActivity(@PathVariable int activityId, @PathVariable int userId) {
        log.info("User (id={}) stops activity with id: {}", userId, activityId);
        return activityService.stopActivity(activityId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{activityId}")
    public ActivityDto updateActivity(@PathVariable int activityId, @RequestBody @Valid ActivityDto activityDto) {
        log.info("Updating activity (id={}): {}", activityId, activityDto);
        return activityService.updateActivity(activityId, activityDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable int activityId) {
        log.info("Deleting activity with id: {}", activityId);
        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();
    }

}
