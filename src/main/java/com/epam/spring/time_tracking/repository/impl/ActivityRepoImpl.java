package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityRepoImpl implements ActivityRepo {

    private final List<Activity> activityList = new ArrayList<>();
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private int idCounter;

    @Override
    public List<Activity> getActivities() {
        log.info("Getting activities");
        return activityList.stream()
                .filter(activity -> activity.getStatus().equals(Activity.Status.BY_ADMIN) ||
                        activity.getStatus().equals(Activity.Status.BY_USER) ||
                        activity.getStatus().equals(Activity.Status.DEL_WAITING))
                .collect(Collectors.toList());
    }

    @Override
    public Activity getActivityById(int activityId) {
        log.info("Getting activity by id: {}", activityId);
        return activityList.stream()
                .filter(activity -> activity.getId() == activityId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("activity is not found"));
    }

    @Override
    public Activity createActivity(Activity activity, boolean isForRequest) {
        log.info("Creating activity: {}", activity);
        log.info("Creating activity for request: {}", isForRequest);
        checkIfUserExists(activity.getCreatorId(), "creator is not found");
        activity.setId(++idCounter);
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            activity.setCategories(List.of(categoryRepo.getCategory(0)));
        activity.setCreateTime(LocalDateTime.now());
        if (isForRequest)
            activity.setStatus(Activity.Status.ADD_WAITING);
        else
            activity.setStatus(Activity.Status.BY_ADMIN);
        activityList.add(activity);
        return activity;
    }

    @Override
    public List<Activity> getAdminActivities(int userId) {
        log.info("Getting activities, which creator (admin) has id: {}", userId);
        checkIfUserExists(userId, "admin is not found");
        return activityList.stream()
                .filter(activity -> activity.getCreatorId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Activity updateActivity(int activityId, Activity activity) {
        log.info("Updating activity (id={}): {}", activityId, activity);
        Activity updatedActivity = getActivityById(activityId);
        updatedActivity.setName(activity.getName());
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            updatedActivity.setCategories(List.of(categoryRepo.getCategory(0)));
        else
            updatedActivity.setCategories(activity.getCategories());
        updatedActivity.setDescription(activity.getDescription());
        updatedActivity.setImage(activity.getImage());
        return updatedActivity;
    }

    @Override
    public void deleteActivity(int activityId) {
        log.info("Deleting activity with id: {}", activityId);
        activityList.removeIf(activity -> activity.getId() == activityId);
    }

    @Override
    public void deleteCategoryInActivities(Category category) {
        log.info("Deleting category in activities: {}", category);
        activityList.stream()
                .filter(activity -> activity.getCategories().contains(category))
                .forEach(activity -> {
                    activity.getCategories().removeIf(c -> c.getId() == category.getId());
                    if (activity.getCategories().isEmpty())
                        activity.getCategories().add(Category.builder().nameEN("Other").nameUA("Інше").build());
                });
    }

    private void checkIfUserExists(int userId, String errorMessage) {
        log.info("Checking if user exists");
        if (!userRepo.checkIfUserExists(userId))
            throw new RuntimeException(errorMessage);
    }
}
