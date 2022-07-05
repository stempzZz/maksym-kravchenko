package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.mapper.ActivityMapper;
import com.epam.spring.time_tracking.mapper.UserActivityMapper;
import com.epam.spring.time_tracking.mapper.UserMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.repository.*;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepo activityRepo;
    private final CategoryRepo categoryRepo;
    private final UserActivityRepo userActivityRepo;
    private final UserRepo userRepo;
    private final RequestRepo requestRepo;

    @Override
    public List<ActivityDto> getActivities() {
        log.info("Getting activities");
        List<Activity> activities = activityRepo.getActivities();
        return activities.stream()
                .map(ActivityMapper.INSTANCE::toActivityDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto getActivity(int activityId) {
        log.info("Getting activity with id: {}", activityId);
        Activity activity = activityRepo.getActivityById(activityId);
        return ActivityMapper.INSTANCE.toActivityDto(activity);
    }

    @Override
    public List<ActivityDto> getActivitiesForUser(int userId) {
        log.info("Getting activities for user with id: {}", userId);
        List<UserActivity> activities = userActivityRepo.getActivitiesForUser(userId);
        return activities.stream()
                .map(UserActivity::getActivity)
                .map(ActivityMapper.INSTANCE::toActivityDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto createActivity(ActivityDto activityDto) {
        log.info("Creating activity: {}", activityDto);

        if (!userRepo.getUserById(activityDto.getCreatorId()).isAdmin())
            throw new RestrictionException(ErrorMessage.INSTANTLY_ACTIVITY_CREATION);

        Activity activity = activityRepo.createActivity(activityWithSetCategories(activityDto));
        return ActivityMapper.INSTANCE.toActivityDto(activity);
    }

    @Override
    public List<UserInActivityDto> getActivityUsers(int activityId) {
        log.info("Getting users for activity with id: {}", activityId);
        List<UserActivity> users = userActivityRepo.getActivityUsers(activityId);
        return users.stream()
                .map(UserActivityMapper.INSTANCE::toUserInActivityDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsersNotInActivity(int activityId) {
        log.info("Getting users who are not in activity with id: {}", activityId);
        List<User> activityUsers = userActivityRepo.getActivityUsers(activityId).stream()
                .map(UserActivity::getUser)
                .collect(Collectors.toList());
        return userRepo.getUsersNotInActivity(activityUsers).stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserInActivityDto getUserInActivity(int activityId, int userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.getUserInActivity(activityId, userId);
        return UserActivityMapper.INSTANCE.toUserInActivityDto(user);
    }

    @Override
    public UserInActivityDto addUserToActivity(int activityId, int userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.addUserToActivity(activityId, userId);
        return UserActivityMapper.INSTANCE.toUserInActivityDto(user);
    }

    @Override
    public void removeUserFromActivity(int activityId, int userId) {
        log.info("Removing user (id={}) from an activity with id: {}", userId, activityId);
        userActivityRepo.removeUserFromActivity(activityId, userId);
    }

    @Override
    public UserInActivityDto startActivity(int activityId, int userId) {
        log.info("User (id={}) starts activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.startActivity(activityId, userId);
        return UserActivityMapper.INSTANCE.toUserInActivityDto(user);
    }

    @Override
    public UserInActivityDto stopActivity(int activityId, int userId) {
        log.info("User (id={}) stops activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.stopActivity(activityId, userId);
        return UserActivityMapper.INSTANCE.toUserInActivityDto(user);
    }

    @Override
    public ActivityDto updateActivity(int activityId, ActivityDto activityDto) {
        log.info("Updating activity (id={}): {}", activityId, activityDto);
        Activity activity = activityRepo.updateActivity(activityId, activityWithSetCategories(activityDto));
        return ActivityMapper.INSTANCE.toActivityDto(activity);
    }

    @Override
    public void deleteActivity(int id) {
        log.info("Deleting activity with id: {}", id);
        userActivityRepo.deleteActivity(id);
        requestRepo.deleteRequestsWithActivity(id);
        activityRepo.deleteActivityById(id);
    }

    private Activity activityWithSetCategories(ActivityDto activityDto) {
        log.info("Setting categories for activity: {}", activityDto);
        Activity activity = ActivityMapper.INSTANCE.fromActividtyDto(activityDto);
        if (activityDto.getCategoryIds() != null) {
            List<Category> categories = activityDto.getCategoryIds().stream()
                    .map(categoryRepo::getCategory)
                    .collect(Collectors.toList());
            activity.setCategories(categories);
        } else {
            activity.setCategories(List.of(categoryRepo.getCategory(0)));
        }
        return activity;
    }
}
