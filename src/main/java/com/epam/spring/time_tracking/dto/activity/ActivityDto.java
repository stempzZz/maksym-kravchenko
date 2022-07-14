package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.group.OnCreate;
import com.epam.spring.time_tracking.dto.group.OnUpdate;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "{validation.not_blank.name}")
    private String name;

    private List<Long> categoryIds;

    @Null(message = "{validation.null.categories}")
    private List<CategoryDto> categories;

    @NotBlank(message = "{validation.not_blank.description}")
    private String description;

    private String image;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int peopleCount;

    @NotNull(message = "'creatorId' shouldn't be empty", groups = OnCreate.class)
    private Long creatorId;

    @Null(message = "{validation.null.create_time}", groups = {OnCreate.class, OnUpdate.class})
    private LocalDateTime createTime;

    @Null(message = "{validation.null.status}", groups = {OnCreate.class, OnUpdate.class})
    private ActivityStatus status;

}
