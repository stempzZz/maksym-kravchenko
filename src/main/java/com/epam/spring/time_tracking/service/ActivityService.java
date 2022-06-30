package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;

import java.util.List;

public interface ActivityService {
    ActivityDto createActivity(ActivityInputDto activityInputDto);

    List<ActivityDto> getActivities();

    ActivityDto updateActivity(int id, ActivityInputDto activityInputDto);

    void deleteActivity(int id);
}
