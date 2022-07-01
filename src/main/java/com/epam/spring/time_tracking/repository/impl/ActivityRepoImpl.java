package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityRepoImpl implements ActivityRepo {

    private final List<Activity> activityList = new ArrayList<>();
    private int idCounter;

    @Override
    public Activity createActivity(Activity activity) {
        activity.setId(++idCounter);
        if (activity.getCategories() == null || activity.getCategories().isEmpty())
            activity.setCategories(List.of(Category.builder().nameEN("Other").nameUA("Інше").build()));
        activity.setCreateTime(LocalDateTime.now());
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
        updatedActivity.setCategories(activity.getCategories());
        updatedActivity.setDescription(activity.getDescription());
        updatedActivity.setImage(activity.getImage());
        return updatedActivity;
    }

    @Override
    public void deleteActivity(int activityId) {
        activityList.removeIf(activity -> activity.getId() == activityId);
    }
}
