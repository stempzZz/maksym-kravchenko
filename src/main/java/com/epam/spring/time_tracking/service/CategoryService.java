package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> getCategories();

    CategoryDto getCategoryById(int id);

    CategoryDto updateCategory(int id, CategoryDto categoryDto);

    void deleteCategory(int id);
}
