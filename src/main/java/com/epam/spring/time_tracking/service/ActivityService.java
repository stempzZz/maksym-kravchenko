package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;

import java.util.List;

public interface ActivityService {

    List<ActivityDto> getActivities();

    ActivityDto getActivity(int activityId);

    List<ActivityForUserDto> getActivitiesForUser(int userId);

    ActivityDto createActivity(ActivityInputDto activityInputDto);

    List<UserInActivityDto> getActivityUsers(int activityId);

    List<UserDto> getUsersNotInActivity(int activityId);

    UserInActivityDto getUserInActivity(int activityId, int userId);

    UserInActivityDto addUserToActivity(int activityId, int userId);

    void removeUserFromActivity(int activityId, int userId);

    UserInActivityDto startActivity(int activityId, int userId);

    UserInActivityDto stopActivity(int activityId, int userId);

    ActivityDto updateActivity(int activityId, ActivityInputDto activityInputDto);

    void deleteActivity(int activityId);
}
