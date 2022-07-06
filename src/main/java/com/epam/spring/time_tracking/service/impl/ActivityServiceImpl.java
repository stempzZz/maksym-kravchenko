package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.*;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Override
    public List<ActivityDto> getActivities() {
        log.info("Getting activities");
        List<Activity> activities = activityRepo.getActivities();
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto getActivity(int activityId) {
        log.info("Getting activity with id: {}", activityId);
        Activity activity = activityRepo.getActivityById(activityId);
        return modelMapper.map(activity, ActivityDto.class);
    }

    @Override
    public List<ActivityForUserDto> getActivitiesForUser(int userId) {
        log.info("Getting activities for user with id: {}", userId);
        List<UserActivity> activities = userActivityRepo.getActivitiesForUser(userId);
        return activities.stream()
                .map(UserActivity::getActivity)
                .map(activity -> modelMapper.map(activity, ActivityForUserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto createActivity(ActivityInputDto activityInputDto) {
        log.info("Creating activity: {}", activityInputDto);
        Activity activity = activityRepo.createActivity(activityWithSetCategories(activityInputDto), false);
        return modelMapper.map(activity, ActivityDto.class);
    }

    @Override
    public List<UserInActivityDto> getActivityUsers(int activityId) {
        log.info("Getting users for activity with id: {}", activityId);
        List<UserActivity> users = userActivityRepo.getActivityUsers(activityId);
        return users.stream()
                .map(userActivity -> modelMapper.map(userActivity, UserInActivityDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getUsersNotInActivity(int activityId) {
        log.info("Getting users who are not in activity with id: {}", activityId);
        List<User> activityUsers = userActivityRepo.getActivityUsers(activityId).stream()
                .map(UserActivity::getUser)
                .collect(Collectors.toList());
        return userRepo.getUsersNotInActivity(activityUsers).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserInActivityDto getUserInActivity(int activityId, int userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.getUserInActivity(activityId, userId);
        return modelMapper.map(user, UserInActivityDto.class);
    }

    @Override
    public UserInActivityDto addUserToActivity(int activityId, int userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.addUserToActivity(activityId, userId);
        return modelMapper.map(user, UserInActivityDto.class);
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
        return modelMapper.map(user, UserInActivityDto.class);
    }

    @Override
    public UserInActivityDto stopActivity(int activityId, int userId) {
        log.info("User (id={}) stops activity with id: {}", userId, activityId);
        UserActivity user = userActivityRepo.stopActivity(activityId, userId);
        return modelMapper.map(user, UserInActivityDto.class);
    }

    @Override
    public ActivityDto updateActivity(int activityId, ActivityInputDto activityInputDto) {
        log.info("Updating activity (id={}): {}", activityId, activityInputDto);
        Activity activity = activityRepo.updateActivity(activityId, activityWithSetCategories(activityInputDto));
        return modelMapper.map(activity, ActivityDto.class);
    }

    @Override
    public void deleteActivity(int id) {
        log.info("Deleting activity with id: {}", id);
        userActivityRepo.deleteActivity(id);
        requestRepo.deleteRequestsWithActivity(id);
        activityRepo.deleteActivity(id);
    }

    private Activity activityWithSetCategories(ActivityInputDto activityInputDto) {
        log.info("Setting categories for activity: {}", activityInputDto);
        Activity activity = modelMapper.map(activityInputDto, Activity.class);
        if (activityInputDto.getCategoryIds() != null) {
            List<Category> categories = activityInputDto.getCategoryIds().stream()
                    .map(categoryRepo::getCategory)
                    .collect(Collectors.toList());
            activity.setCategories(categories);
        } else {
            activity.setCategories(List.of(categoryRepo.getCategory(0)));
        }
        return activity;
    }

}
