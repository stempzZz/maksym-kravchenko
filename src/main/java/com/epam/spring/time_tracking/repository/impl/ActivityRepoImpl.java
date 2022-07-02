package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityRepoImpl implements ActivityRepo {

    private final List<Activity> activityList = new ArrayList<>();
    private final UserRepo userRepo;
    private int idCounter;

    @Override
    public Activity createActivity(Activity activity) {
        checkIfUserExists(activity.getCreatorId(), "creator is not found");
        activity.setId(++idCounter);
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            activity.setCategories(List.of(Category.builder().nameEN("Other").nameUA("Інше").build()));
        activity.setCreateTime(LocalDateTime.now());
        activity.setStatus(Activity.Status.BY_ADMIN);
        activityList.add(activity);
        return activity;
    }

    @Override
    public Activity createActivityForRequest(Activity activity) {
        checkIfUserExists(activity.getCreatorId(), "creator is not found");
        activity.setId(++idCounter);
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            activity.setCategories(List.of(Category.builder().nameEN("Other").nameUA("Інше").build()));
        activity.setCreateTime(LocalDateTime.now());
        activity.setStatus(Activity.Status.ADD_WAITING);
        activityList.add(activity);
        return activity;
    }

    @Override
    public List<Activity> getActivities() {
        return activityList.stream()
                .filter(activity -> activity.getStatus().equals(Activity.Status.BY_ADMIN) ||
                        activity.getStatus().equals(Activity.Status.BY_USER) ||
                        activity.getStatus().equals(Activity.Status.DEL_WAITING))
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getAdminActivities(int userId) {
        checkIfUserExists(userId, "admin is not found");
        return activityList.stream()
                .filter(activity -> activity.getCreatorId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Activity getActivityById(int activityId) {
        return activityList.stream()
                .filter(activity -> activity.getId() == activityId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("activity is not found"));
    }

    @Override
    public Activity updateActivity(int activityId, Activity activity) {
        checkIfUserExists(activity.getCreatorId(), "creator is not found");
        Activity updatedActivity = getActivityById(activityId);
        updatedActivity.setName(activity.getName());
        updatedActivity.setCategories(activity.getCategories());
        updatedActivity.setDescription(activity.getDescription());
        updatedActivity.setImage(activity.getImage());
        return updatedActivity;
    }

    @Override
    public void deleteActivity(int activityId) {
        activityList.removeIf(activity -> activity.getId() == activityId);
    }

    private void checkIfUserExists(int userId, String errorMessage) {
        if (!userRepo.checkIfUserExists(userId))
            throw new RuntimeException(errorMessage);
    }
}
