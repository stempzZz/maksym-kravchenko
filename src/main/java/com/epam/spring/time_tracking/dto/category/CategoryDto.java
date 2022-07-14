package com.epam.spring.time_tracking.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "{validation.not_blank.name_en}")
    private String nameEn;

    @NotBlank(message = "{validation.not_blank.name_ua}")
    private String nameUa;

}
