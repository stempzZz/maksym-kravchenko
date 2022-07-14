package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.dto.request.RequestForListDto;
import com.epam.spring.time_tracking.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "activity.creator", target = "user")
    @Mapping(source = "activity", target = "activity")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "forDelete", target = "forDelete")
    @Mapping(source = "createTime", target = "createTime")
    RequestDto toRequestDto(Request request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "activity.creator", target = "user")
    @Mapping(source = "activity.id", target = "activity.id")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "forDelete", target = "forDelete")
    @Mapping(source = "createTime", target = "createTime")
    RequestForListDto toRequestDtoForList(Request request);

}
