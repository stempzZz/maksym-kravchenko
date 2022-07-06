package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityForUserDto {

    private int id;
    private String name;
    private List<CategoryDto> categories;
    private String description;
    private String image;
    private int peopleCount;
    private LocalDateTime createTime;

}
