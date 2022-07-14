package com.epam.spring.time_tracking.dto.category;

import lombok.Data;

@Data
public class CategoryForListDto {

    private Long id;
    private String nameEn;
    private String nameUa;
    private boolean isDefault;

}
