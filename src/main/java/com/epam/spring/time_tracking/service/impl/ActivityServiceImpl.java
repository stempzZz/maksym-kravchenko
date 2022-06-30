package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityInputDto;
import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepo activityRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    @Override
    public ActivityDto createActivity(ActivityInputDto activityInputDto) {
        List<Category> categories = activityInputDto.getCategoryIds().stream()
                .map(categoryRepo::getCategoryById)
                .collect(Collectors.toList());
        Activity activity = modelMapper.map(activityInputDto, Activity.class);
        activity.setCategories(categories);

        activity = activityRepo.createActivity(activity);

        List<CategoryDto> categoryDtos = activity.getCategories().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setCategories(categoryDtos);
        return activityDto;
    }

    @Override
    public List<ActivityDto> getActivities() {
        List<Activity> activities = activityRepo.getActivities();
        return activities.stream()
                .map(activity -> {
                    List<CategoryDto> categoryDtos = activity.getCategories().stream()
                            .map(category -> modelMapper.map(category, CategoryDto.class))
                            .collect(Collectors.toList());
                    ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
                    activityDto.setCategories(categoryDtos);
                    return activityDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto updateActivity(int id, ActivityInputDto activityInputDto) {
        List<Category> categories = activityInputDto.getCategoryIds().stream()
                .map(categoryRepo::getCategoryById)
                .collect(Collectors.toList());
        Activity activity = modelMapper.map(activityInputDto, Activity.class);
        activity.setCategories(categories);

        activity = activityRepo.updateActivity(id, activity);

        List<CategoryDto> categoryDtos = activity.getCategories().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
        ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
        activityDto.setCategories(categoryDtos);
        return activityDto;
    }

    @Override
    public void deleteActivity(int id) {
        activityRepo.deleteActivity(id);
    }
}
