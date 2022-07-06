package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.api.ActivityApi;
import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ActivityController implements ActivityApi {

    private final ActivityService activityService;

    @Override
    public List<ActivityDto> getActivities() {
        log.info("Getting all available activities");
        return activityService.getActivities();
    }

    @Override
    public ActivityDto getActivity(int activityId) {
        log.info("Getting activity with id: {}", activityId);
        return activityService.getActivity(activityId);
    }

    @Override
    public List<ActivityDto> getActivitiesForUser(int userId) {
        log.info("Getting activities for regular user with id: {}", userId);
        return activityService.getActivitiesForUser(userId);
    }

    @Override
    public ActivityDto createActivity(ActivityDto activityDto) {
        log.info("Creating activity: {}", activityDto);
        return activityService.createActivity(activityDto);
    }

    @Override
    public List<UserInActivityDto> getActivityUsers(int activityId) {
        log.info("Getting users for activity with id: {}", activityId);
        return activityService.getActivityUsers(activityId);
    }

    @Override
    public List<UserDto> getUsersNotInActivity(int activityId) {
        log.info("Getting users who are not in activity with id: {}", activityId);
        return activityService.getUsersNotInActivity(activityId);
    }

    @Override
    public UserInActivityDto getUserInActivity(int activityId, int userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);
        return activityService.getUserInActivity(activityId, userId);
    }

    @Override
    public UserInActivityDto addUserToActivity(int activityId, int userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);
        return activityService.addUserToActivity(activityId, userId);
    }

    @Override
    public ResponseEntity<Void> removeUserFromActivity(int activityId, int userId) {
        log.info("Removing user (id={}) from an activity with id: {}", userId, activityId);
        activityService.removeUserFromActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public UserInActivityDto startActivity(int activityId, int userId) {
        log.info("User (id={}) starts activity with id: {}", userId, activityId);
        return activityService.startActivity(activityId, userId);
    }

    @Override
    public UserInActivityDto stopActivity(int activityId, int userId) {
        log.info("User (id={}) stops activity with id: {}", userId, activityId);
        return activityService.stopActivity(activityId, userId);
    }

    @Override
    public ActivityDto updateActivity(int activityId, ActivityDto activityDto) {
        log.info("Updating activity (id={}): {}", activityId, activityDto);
        return activityService.updateActivity(activityId, activityDto);
    }

    @Override
    public ResponseEntity<Void> deleteActivity(int activityId) {
        log.info("Deleting activity with id: {}", activityId);
        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();
    }

}
