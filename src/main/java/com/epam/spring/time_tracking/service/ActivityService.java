package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityService {

    List<ActivityDto> getActivities(Pageable pageable);

    ActivityDto getActivity(Long activityId);

    List<ActivityDto> getActivitiesForUser(Long userId, Pageable pageable);

    ActivityDto createActivity(ActivityDto activityDto);

    List<UserInActivityDto> getActivityUsers(Long activityId, Pageable pageable);

    List<UserOnlyNameDto> getUsersNotInActivity(Long activityId);

    UserInActivityDto getUserInActivity(Long activityId, Long userId);

    UserInActivityDto addUserToActivity(Long activityId, Long userId);

    void removeUserFromActivity(Long activityId, Long userId);

    UserInActivityDto startActivity(Long activityId, Long userId);

    UserInActivityDto stopActivity(Long activityId, Long userId);

    ActivityDto updateActivity(Long activityId, ActivityDto activityDto);

    void deleteActivity(Long activityId);

}
