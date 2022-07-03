package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getCategories();

    CategoryDto getCategory(int categoryId);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(int categoryId, CategoryDto categoryDto);

    void deleteCategory(int categoryId);
}
