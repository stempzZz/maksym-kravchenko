package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;

public abstract class UpdateMapper {

    public static Activity updateActivityWithPresentActivityDtoFields(Activity activity, ActivityDto activityDto) {
        if (activity == null)
            return null;

        activity.setName(activityDto.getName());
        activity.setDescription(activityDto.getDescription());
        activity.setImage(activityDto.getImage());

        return activity;
    }

    public static Category updateCategoryWithPresentCategoryDtoFields(Category category, CategoryDto categoryDto) {
        if (category == null)
            return null;

        category.setNameEn(categoryDto.getNameEn());
        category.setNameUa(categoryDto.getNameUa());

        return category;
    }

    public static User updateUserInformationWithPresentUserDtoFields(User user, UserDto userDto) {
        if (user == null)
            return null;

        user.setLastName(userDto.getLastName());
        user.setFirstName(userDto.getFirstName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static User updateUserPasswordWithPresentUserDtoFields(User user, UserDto userDto) {
        if (user == null)
            return null;

        user.setPassword(userDto.getPassword());

        return user;
    }

}
