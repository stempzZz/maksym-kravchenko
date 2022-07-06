package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.model.Activity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActivityMapper {

    ActivityMapper INSTANCE = Mappers.getMapper(ActivityMapper.class);

    ActivityDto toActivityDto(Activity activity);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "peopleCount", target = "peopleCount")
    ActivityDto toActivityDtoForAdminProfile(Activity activity);

    Activity fromActividtyDto(ActivityDto activityDto);

}
