package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.model.Activity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDto {

    private int id;
    private String name;
    private List<CategoryDto> categories;
    private String description;
    private String image;
    private int peopleCount;
    private int creatorId;
    private LocalDateTime createTime;
    private Activity.Status status;

}
