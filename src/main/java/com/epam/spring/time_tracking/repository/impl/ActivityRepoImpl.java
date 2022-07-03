package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.repository.*;
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
    public Activity createActivity(Activity activity, boolean isForRequest) {
        checkIfUserExists(activity.getCreatorId(), "creator is not found");
        activity.setId(++idCounter);
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            activity.setCategories(List.of(Category.builder().nameEN("Other").nameUA("Інше").build()));
        activity.setCreateTime(LocalDateTime.now());
        if (isForRequest)
            activity.setStatus(Activity.Status.ADD_WAITING);
        else
            activity.setStatus(Activity.Status.BY_ADMIN);
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
        Activity updatedActivity = getActivityById(activityId);
        updatedActivity.setName(activity.getName());
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            updatedActivity.setCategories(List.of(Category.builder().nameEN("Other").nameUA("Інше").build()));
        else
            updatedActivity.setCategories(activity.getCategories());
        updatedActivity.setDescription(activity.getDescription());
        updatedActivity.setImage(activity.getImage());
        return updatedActivity;
    }

    @Override
    public void deleteActivity(int activityId) {
        activityList.removeIf(activity -> activity.getId() == activityId);
    }

    @Override
    public void deleteCategoryInActivities(Category category) {
        activityList.stream()
                .filter(activity -> activity.getCategories().contains(category))
                .forEach(activity -> {
                    activity.getCategories().removeIf(c -> c.getId() == category.getId());
                    if (activity.getCategories().isEmpty())
                        activity.getCategories().add(Category.builder().nameEN("Other").nameUA("Інше").build());
                });
    }

    private void checkIfUserExists(int userId, String errorMessage) {
        if (!userRepo.checkIfUserExists(userId))
            throw new RuntimeException(errorMessage);
    }

    private void checkIfUserIsAdmin(int userId) {
        if (!userRepo.checkIfUserIsAdmin(userId))
            throw new RuntimeException("regular user can't create activity immediately");
    }
}
