package com.epam.spring.time_tracking.mapper;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import com.epam.spring.time_tracking.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto toCategoryDto(Category category);

    CategoryForListDto toCategoryForListDto(Category category);

    Category fromCategoryDto(CategoryDto categoryDto);

}
