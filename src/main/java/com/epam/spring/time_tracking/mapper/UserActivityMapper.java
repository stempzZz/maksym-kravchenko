package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserActivityMapper {

    UserActivityMapper INSTANCE = Mappers.getMapper(UserActivityMapper.class);

    @Mapping(source = "userActivity.activity.id", target = "activityId")
    @Mapping(source = "userActivity.user", target = "user")
    @Mapping(source = "userActivity.spentTime", target = "spentTime")
    UserInActivityDto toUserInActivityDto(UserActivity userActivity);

    @Mapping(source = "userActivity.activity.id", target = "id")
    @Mapping(source = "userActivity.activity.name", target = "name")
    ActivityForUserProfileDto toActivityForUserProfileDto(UserActivity userActivity);

}
