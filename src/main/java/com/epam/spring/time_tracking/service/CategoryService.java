package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import com.epam.spring.time_tracking.model.Category;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    List<CategoryForListDto> getCategories(Pageable pageable);

    CategoryDto getCategory(Long categoryId);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    List<Category> mapCategoriesIdsToCategories(List<Long> categoriesIds);

}
