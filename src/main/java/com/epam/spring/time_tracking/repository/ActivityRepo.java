package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.UserActivity;

import java.util.List;

public interface ActivityRepo {
    Activity createActivity(Activity activity);

    List<Activity> getActivities();

    List<Activity> getAdminActivities(int userId);

    Activity getActivityById(int activityId);

    Activity updateActivity(int activityId, Activity activity);

    void deleteActivity(int activityId);
}
