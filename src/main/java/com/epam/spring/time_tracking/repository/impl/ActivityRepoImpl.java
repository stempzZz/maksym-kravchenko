package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityRepoImpl implements ActivityRepo {

    private final List<Activity> activityList = new ArrayList<>();
    private int idCounter;

    @Override
    public Activity createActivity(Activity activity) {
        activity.setId(++idCounter);
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
    public Activity getActivityById(int id) {
        return activityList.stream()
                .filter(activity -> activity.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("activity is not found"));
    }

    @Override
    public Activity updateActivity(int id, Activity activity) {
        Activity updatedActivity = getActivityById(id);
        updatedActivity.setName(activity.getName());
        updatedActivity.setCategories(activity.getCategories());
        updatedActivity.setDescription(activity.getDescription());
        updatedActivity.setImage(activity.getImage());
        return updatedActivity;
    }

    @Override
    public void deleteActivity(int id) {
        activityList.removeIf(activity -> activity.getId() == id);
    }
}
