package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "request.id", target = "id")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "activity", target = "activity")
    @Mapping(source = "request.status", target = "status")
    @Mapping(source = "request.forDelete", target = "forDelete")
    @Mapping(source = "request.createTime", target = "createTime")
    RequestDto toRequestDto(Request request, Activity activity, User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "request.id", target = "id")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "activity.id", target = "activity.id")
    @Mapping(source = "activity.name", target = "activity.name")
    @Mapping(source = "request.status", target = "status")
    @Mapping(source = "request.forDelete", target = "forDelete")
    @Mapping(source = "request.createTime", target = "createTime")
    RequestDto toRequestDtoForList(Request request, Activity activity, User user);

}
