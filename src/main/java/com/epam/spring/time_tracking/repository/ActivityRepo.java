package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;

import java.util.List;

public interface ActivityRepo {

    List<Activity> getActivities();

    Activity getActivityById(int activityId);

    Activity createActivity(Activity activity);

    List<Activity> getActivitiesCreatedByUser(int userId, boolean checkForAdmin);

    Activity updateActivity(int activityId, Activity activity);

    void deleteActivityById(int activityId);

    void deleteCategoryInActivities(Category category);
}
