package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.api.ActivityApi;
import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ActivityController implements ActivityApi {

    private final ActivityService activityService;

    @Override
    public List<ActivityDto> getActivities(Pageable pageable) {
        log.info("Getting all available activities");
        return activityService.getActivities(pageable);
    }

    @Override
    public ActivityDto getActivity(Long activityId) {
        log.info("Getting activity with id: {}", activityId);
        return activityService.getActivity(activityId);
    }

    @Override
    public List<ActivityDto> getActivitiesForUser(Long userId, Pageable pageable) {
        log.info("Getting activities for regular user with id: {}", userId);
        return activityService.getActivitiesForUser(userId, pageable);
    }

    @Override
    public ActivityDto createActivity(ActivityDto activityDto) {
        log.info("Creating activity: {}", activityDto);
        return activityService.createActivity(activityDto);
    }

    @Override
    public List<UserInActivityDto> getActivityUsers(Long activityId, Pageable pageable) {
        log.info("Getting users for activity with id: {}", activityId);
        return activityService.getActivityUsers(activityId, pageable);
    }

    @Override
    public List<UserOnlyNameDto> getUsersNotInActivity(Long activityId) {
        log.info("Getting users who are not in activity with id: {}", activityId);
        return activityService.getUsersNotInActivity(activityId);
    }

    @Override
    public UserInActivityDto getUserInActivity(Long activityId, Long userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);
        return activityService.getUserInActivity(activityId, userId);
    }

    @Override
    public UserInActivityDto addUserToActivity(Long activityId, Long userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);
        return activityService.addUserToActivity(activityId, userId);
    }

    @Override
    public ResponseEntity<Void> removeUserFromActivity(Long activityId, Long userId) {
        log.info("Removing user (id={}) from an activity with id: {}", userId, activityId);
        activityService.removeUserFromActivity(activityId, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public UserInActivityDto startActivity(Long activityId, Long userId) {
        log.info("User (id={}) starts activity with id: {}", userId, activityId);
        return activityService.startActivity(activityId, userId);
    }

    @Override
    public UserInActivityDto stopActivity(Long activityId, Long userId) {
        log.info("User (id={}) stops activity with id: {}", userId, activityId);
        return activityService.stopActivity(activityId, userId);
    }

    @Override
    public ActivityDto updateActivity(Long activityId, ActivityDto activityDto) {
        log.info("Updating activity (id={}): {}", activityId, activityDto);
        return activityService.updateActivity(activityId, activityDto);
    }

    @Override
    public ResponseEntity<Void> deleteActivity(Long activityId) {
        log.info("Deleting activity with id: {}", activityId);
        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();
    }

}
