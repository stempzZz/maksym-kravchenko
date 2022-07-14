package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityShortInfoDto {

    private Long id;
    private String name;
    private List<CategoryDto> categories;
    private String description;
    private String image;

}
