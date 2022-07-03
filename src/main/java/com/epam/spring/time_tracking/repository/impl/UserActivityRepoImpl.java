package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityRepoImpl implements UserActivityRepo {

    private final List<UserActivity> userActivityList = new ArrayList<>();
    private final ActivityRepo activityRepo;
    private final UserRepo userRepo;

    @Override
    public List<UserActivity> getActivityUsers(int activityId) {
        log.info("Getting users in activity with id: {}", activityId);
        return userActivityList.stream()
                .filter(userActivity -> userActivity.getActivity().getId() == activityId)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserActivity> getActivitiesForUser(int userId) {
        log.info("Getting activities for user with id: {}", userId);
        return userActivityList.stream()
                .filter(userActivity -> userActivity.getUser().getId() == userId)
                .filter(userActivity -> userActivity.getActivity().getStatus().equals(Activity.Status.BY_ADMIN) ||
                        userActivity.getActivity().getStatus().equals(Activity.Status.BY_USER) ||
                        userActivity.getActivity().getStatus().equals(Activity.Status.DEL_WAITING))
                .collect(Collectors.toList());
    }

    @Override
    public UserActivity getUserInActivity(int activityId, int userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);
        return userActivityList.stream()
                .filter(userActivity -> userActivity.getActivity().getId() == activityId &&
                        userActivity.getUser().getId() == userId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("user doesn't exist in activity"));
    }

    @Override
    public UserActivity addUserToActivity(int activityId, int userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);
        checkIfUserNotInActivity(activityId, userId);
        Activity activity = activityRepo.getActivityById(activityId);
        checkIfActivityIsAvailable(activity);
        User user = userRepo.getUserById(userId);
        if (user.isAdmin())
            throw new RuntimeException("admin can't be in activity");
        UserActivity userActivity = UserActivity.builder()
                .activity(activity)
                .user(user)
                .status(UserActivity.Status.NOT_STARTED)
                .build();
        userActivityList.add(userActivity);
        activity.setPeopleCount(activity.getPeopleCount() + 1);
        user.setActivityCount(user.getActivityCount() + 1);
        return userActivity;
    }

    @Override
    public void removeUserFromActivity(int activityId, int userId) {
        log.info("Removing user (id={}) from an activity with id: {}", userId, activityId);
        User user = getUserInActivity(activityId, userId).getUser();
        Activity activity = activityRepo.getActivityById(activityId);
        activity.setPeopleCount(activity.getPeopleCount() - 1);
        user.setActivityCount(user.getActivityCount() - 1);
        userActivityList.removeIf(userActivity -> userActivity.getActivity().getId() == activityId &&
                userActivity.getUser().getId() == userId);
    }

    @Override
    public void removeUserFromActivities(int userId) {
        log.info("Removing user (id={}) from activities", userId);
        List<Activity> activities = userActivityList.stream()
                .filter(userActivity -> userActivity.getUser().getId() == userId)
                .map(UserActivity::getActivity)
                .collect(Collectors.toList());
        if (!activities.isEmpty())
            activities.forEach(activity -> activity.setPeopleCount(activity.getPeopleCount() - 1));
        userActivityList.removeIf(userActivity -> userActivity.getUser().getId() == userId);
    }

    @Override
    public UserActivity startActivity(int activityId, int userId) {
        log.info("Starting activity (id={}) by user with id: {}", activityId, userId);
        UserActivity userActivity = getUserInActivity(activityId, userId);
        userActivity.setStartTime(LocalDateTime.now());
        userActivity.setStopTime(null);
        userActivity.setStatus(UserActivity.Status.STARTED);
        return userActivity;
    }

    @Override
    public UserActivity stopActivity(int activityId, int userId) {
        log.info("Stopping activity (id={}) by user with id: {}", activityId, userId);
        UserActivity userActivity = getUserInActivity(activityId, userId);
        userActivity.setStopTime(LocalDateTime.now());

        double spentTime = Duration.between(userActivity.getStartTime(), userActivity.getStopTime()).toMillis();
        spentTime = Double.parseDouble(String.format("%.1f", spentTime / 1000.0/ 60.0/ 60.0));

        userActivity.setSpentTime(userActivity.getSpentTime() + spentTime);
        userActivity.getUser().setSpentTime(userActivity.getUser().getSpentTime() + spentTime);
        userActivity.setStatus(UserActivity.Status.STOPPED);
        return userActivity;
    }

    @Override
    public void deleteActivity(int activityId) {
        log.info("Deleting activity with id: {}", activityId);
        userActivityList.stream()
                .filter(userActivity -> userActivity.getActivity().getId() == activityId)
                .forEach(userActivity -> {
                    User user = userActivity.getUser();
                    user.setActivityCount(user.getActivityCount() - 1);
                });
        userActivityList.removeIf(userActivity -> userActivity.getActivity().getId() == activityId);
    }

    private void checkIfUserNotInActivity(int activityId, int userId) {
        log.info("Checking if user (id={}) not in activity with id: {}", userId, activityId);
        Optional<UserActivity> userActivity = userActivityList.stream()
                .filter(ua -> ua.getActivity().getId() == activityId &&
                        ua.getUser().getId() == userId)
                .findFirst();
        if (userActivity.isPresent())
            throw new RuntimeException("user is already in activity");
    }

    private void checkIfActivityIsAvailable(Activity activity) {
        log.info("Checking if activity is available: {}", activity);
        if (activity.getStatus().equals(Activity.Status.ADD_WAITING) ||
                activity.getStatus().equals(Activity.Status.ADD_DECLINED) ||
                activity.getStatus().equals(Activity.Status.DEL_CONFIRMED)) {
            throw new RuntimeException("activity is not available or removed");
        }
    }
}
