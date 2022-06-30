package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;

import java.util.List;

public interface ActivityRepo {
    Activity createActivity(Activity activity);

    List<Activity> getActivities();

    Activity getActivityById(int id);

    Activity updateActivity(int id, Activity activity);

    void deleteActivity(int id);
}
