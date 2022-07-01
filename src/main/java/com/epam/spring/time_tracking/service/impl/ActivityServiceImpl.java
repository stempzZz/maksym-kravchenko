package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepo activityRepo;
    private final CategoryRepo categoryRepo;
    private final UserActivityRepo userActivityRepo;
    private final ModelMapper modelMapper;

    @Override
    public ActivityDto createActivity(ActivityInputDto activityInputDto) {
        Activity activity = activityRepo.createActivity(activityWithSetCategories(activityInputDto));
        return modelMapper.map(activity, ActivityDto.class);
    }

    @Override
    public List<ActivityDto> getActivities() {
        List<Activity> activities = activityRepo.getActivities();
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto getActivity(int activityId) {
        Activity activity = activityRepo.getActivityById(activityId);
        return modelMapper.map(activity, ActivityDto.class);
    }

    @Override
    public ActivityDto updateActivity(int activityId, ActivityInputDto activityInputDto) {
        Activity activity = activityRepo.updateActivity(activityId, activityWithSetCategories(activityInputDto));
        return modelMapper.map(activity, ActivityDto.class);
    }

    @Override
    public void deleteActivity(int id) {
        activityRepo.deleteActivity(id);
    }

    @Override
    public List<ActivityForUserDto> getActivitiesForUser(int userId) {
        List<UserActivity> activities = userActivityRepo.getActivitiesForUser(userId);
        return activities.stream()
                .map(UserActivity::getActivity)
                .map(activity -> modelMapper.map(activity, ActivityForUserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserInActivityDto> getActivityUsers(int activityId) {
        List<UserActivity> users = userActivityRepo.getActivityUsers(activityId);
        return users.stream()
                .map(userActivity -> modelMapper.map(userActivity, UserInActivityDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserInActivityDto addUserToActivity(int activityId, int userId) {
        UserActivity user = userActivityRepo.addUserToActivity(activityId, userId);
        return modelMapper.map(user, UserInActivityDto.class);
    }

    @Override
    public void removeUserFromActivity(int activityId, int userId) {
        userActivityRepo.removeUserFromActivity(activityId, userId);
    }

    @Override
    public UserInActivityDto getUserInActivity(int activityId, int userId) {
        UserActivity user = userActivityRepo.getUserInActivity(activityId, userId);
        return modelMapper.map(user, UserInActivityDto.class);
    }

    private Activity activityWithSetCategories(ActivityInputDto activityInputDto) {
        Activity activity = modelMapper.map(activityInputDto, Activity.class);
        if (activityInputDto.getCategoryIds() != null) {
            List<Category> categories = activityInputDto.getCategoryIds().stream()
                    .map(categoryRepo::getCategoryById)
                    .collect(Collectors.toList());
            activity.setCategories(categories);
        } else {
            activity.setCategories(List.of(categoryRepo.getCategoryById(0)));
        }
        return activity;
    }
}
