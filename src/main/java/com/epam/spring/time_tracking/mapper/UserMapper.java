package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import com.epam.spring.time_tracking.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(User user);

    UserOnlyNameDto toUserUserOnlyNameDto(User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "activityCount", target = "activityCount")
    @Mapping(source = "spentTime", target = "spentTime")
    @Mapping(source = "admin", target = "admin")
    @Mapping(source = "blocked", target = "blocked")
    UserDto toUserDtoForShowingInformation(User user);

    User fromUserDto(UserDto userDto);

}
