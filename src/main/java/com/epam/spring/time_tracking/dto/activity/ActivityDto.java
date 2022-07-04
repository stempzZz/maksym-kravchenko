package com.epam.spring.time_tracking.dto.activity;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.group.OnCreate;
import com.epam.spring.time_tracking.dto.group.OnUpdate;
import com.epam.spring.time_tracking.model.Activity;
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
    private int id;

    @NotBlank(message = "'name' shouldn't be empty")
    private String name;

    private List<Integer> categoryIds;

    @Null(message = "'categories' should be absent in request")
    private List<CategoryDto> categories;

    @NotBlank(message = "'description' shouldn't be empty")
    private String description;

    private String image;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int peopleCount;

    @NotNull(message = "'creatorId' shouldn't be empty", groups = OnCreate.class)
    private int creatorId;

    @Null(message = "'createTime' should be absent in request", groups = {OnCreate.class, OnUpdate.class})
    private LocalDateTime createTime;

    @Null(message = "'status' should be absent in request", groups = {OnCreate.class, OnUpdate.class})
    private Activity.Status status;
}
