package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.model.Category;

import java.util.List;

public interface CategoryRepo {
    Category createCategory(Category category);

    List<Category> getCategories();

    Category getCategoryById(int id);

    Category updateCategory(int id, Category category);

    void deleteCategory(int id);
}
