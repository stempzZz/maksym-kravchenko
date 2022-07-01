package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.model.Category;

import java.util.List;

public interface CategoryRepo {
    Category createCategory(Category category);

    List<Category> getCategories();

    Category getCategoryById(int categoryId);

    Category updateCategory(int categoryId, Category category);

    void deleteCategory(int categoryId);
}
