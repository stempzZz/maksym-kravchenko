package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.activity.ActivityViewDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;

import java.util.List;

public interface ActivityService {
    ActivityDto createActivity(ActivityInputDto activityInputDto);

    List<ActivityDto> getActivities();

    ActivityDto getActivity(int activityId);

    ActivityDto updateActivity(int activityId, ActivityInputDto activityInputDto);

    void deleteActivity(int activityId);

    List<UserInActivityDto> getActivityUsers(int activityId);

    UserInActivityDto addUserToActivity(int activityId, int userId);

    void removeUserFromActivity(int activityId, int userId);
}
