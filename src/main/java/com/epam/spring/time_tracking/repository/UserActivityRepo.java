package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.UserActivity;

import java.util.List;

public interface UserActivityRepo {

    List<UserActivity> getActivityUsers(int activityId);

    UserActivity addUserToActivity(int activityId, int userId);

    void removeUserFromActivity(int activityId, int userId);
}
