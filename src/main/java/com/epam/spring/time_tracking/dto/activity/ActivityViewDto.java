package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import lombok.Data;

import java.util.List;

@Data
public class ActivityViewDto {
    private ActivityDto activity;
    private List<UserInActivityDto> users;
}
