package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserActivityRepoImpl implements UserActivityRepo {

    private final List<UserActivity> userActivityList = new ArrayList<>();
    private final ActivityRepo activityRepo;
    private final UserRepo userRepo;

    @Override
    public List<UserActivity> getActivityUsers(int activityId) {
        return userActivityList.stream()
                .filter(userActivity -> userActivity.getActivity().getId() == activityId)
                .collect(Collectors.toList());
    }

    @Override
    public UserActivity addUserToActivity(int activityId, int userId) {
        Activity activity = activityRepo.getActivityById(activityId);
        User user = userRepo.getUserById(userId);
        UserActivity userActivity = UserActivity.builder()
                .activity(activity)
                .user(user)
                .status(UserActivity.Status.NOT_STARTED)
                .build();
        userActivityList.add(userActivity);
        return userActivity;
    }

    @Override
    public void removeUserFromActivity(int activityId, int userId) {
        userActivityList.removeIf(userActivity -> userActivity.getActivity().getId() == activityId &&
                userActivity.getUser().getId() == userId);
    }

    @Override
    public List<UserActivity> getActivitiesForUser(int userId) {
        return userActivityList.stream()
                .filter(userActivity -> userActivity.getUser().getId() == userId)
                .filter(userActivity -> userActivity.getActivity().getStatus().equals(Activity.Status.BY_ADMIN) ||
                        userActivity.getActivity().getStatus().equals(Activity.Status.BY_USER) ||
                        userActivity.getActivity().getStatus().equals(Activity.Status.DEL_WAITING))
                .collect(Collectors.toList());
    }

    @Override
    public UserActivity getUserInActivity(int activityId, int userId) {
        return userActivityList.stream()
                .filter(userActivity -> userActivity.getActivity().getId() == activityId &&
                        userActivity.getUser().getId() == userId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("user doesn't exist in activity"));
    }

    @Override
    public UserActivity startActivity(int activityId, int userId) {
        UserActivity userActivity = getUserInActivity(activityId, userId);
        userActivity.setStartTime(LocalDateTime.now());
        userActivity.setStopTime(null);
        userActivity.setStatus(UserActivity.Status.STARTED);
        return userActivity;
    }

    @Override
    public UserActivity stopActivity(int activityId, int userId) {
        UserActivity userActivity = getUserInActivity(activityId, userId);
        userActivity.setStopTime(LocalDateTime.now());

        double spentTime = Duration.between(userActivity.getStartTime(), userActivity.getStopTime()).toMillis();
        spentTime = Double.parseDouble(String.format("%.1f", spentTime / 1000.0/ 60.0/ 60.0));

        userActivity.setSpentTime(userActivity.getSpentTime() + spentTime);
        userActivity.getUser().setSpentTime(userActivity.getUser().getSpentTime() + spentTime);
        userActivity.setStatus(UserActivity.Status.STOPPED);
        return userActivity;
    }
}
