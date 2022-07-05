package com.epam.spring.time_tracking.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @NotBlank(message = "'nameEN' shouldn't be empty")
    private String nameEN;

    @NotBlank(message = "'nameUA' shouldn't be empty")
    private String nameUA;
}
