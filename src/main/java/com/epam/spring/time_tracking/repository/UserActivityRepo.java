package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.UserActivity;

import java.util.List;

public interface UserActivityRepo {

    List<UserActivity> getActivityUsers(int activityId);

    List<UserActivity> getActivitiesForUser(int userId);

    UserActivity getUserInActivity(int activityId, int userId);

    UserActivity addUserToActivity(int activityId, int userId);

    void removeUserFromActivity(int activityId, int userId);

    void removeUserFromActivities(int userId);

    UserActivity startActivity(int activityId, int userId);

    UserActivity stopActivity(int activityId, int userId);

    void deleteActivity(int activityId);
}
