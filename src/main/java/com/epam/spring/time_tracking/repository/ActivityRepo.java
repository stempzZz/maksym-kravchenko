package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;

import java.util.List;

public interface ActivityRepo {

    List<Activity> getActivities();

    Activity getActivityById(int activityId);

    Activity createActivity(Activity activity);

    List<Activity> getAdminActivities(int userId);

    Activity updateActivity(int activityId, Activity activity);

    void deleteActivity(int activityId);

    void deleteCategoryInActivities(Category category);
}
