package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.model.ActivityUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActivityUserMapper {

    ActivityUserMapper INSTANCE = Mappers.getMapper(ActivityUserMapper.class);

    @Mapping(source = "activityUser.activity.id", target = "activityId")
    @Mapping(source = "activityUser.user", target = "user")
    @Mapping(source = "activityUser.spentTime", target = "spentTime")
    UserInActivityDto toUserInActivityDto(ActivityUser activityUser);

    @Mapping(source = "activityUser.activity.id", target = "id")
    @Mapping(source = "activityUser.activity.name", target = "name")
    ActivityForUserProfileDto toActivityForUserProfileDto(ActivityUser activityUser);

}
