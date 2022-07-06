package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Category;

import java.util.List;

public interface CategoryRepo {

    List<Category> getCategories();

    Category getCategory(int categoryId);

    Category createCategory(Category category);

    Category updateCategory(int categoryId, Category category);

    void deleteCategory(int categoryId);

}
