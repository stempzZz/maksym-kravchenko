package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryRepoImpl implements CategoryRepo {

    private final List<Category> categoryList = new ArrayList<>();
    private int idCounter;

    @Override
    public Category createCategory(Category category) {
        category.setId(++idCounter);
        categoryList.add(category);
        return category;
    }

    @Override
    public List<Category> getCategories() {
        return categoryList;
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryList.stream()
                .filter(category -> category.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("category is not found"));
    }

    @Override
    public Category updateCategory(int id, Category category) {
        Category updatedCategory = getCategoryById(id);
        updatedCategory.setNameEN(category.getNameEN());
        updatedCategory.setNameUA(category.getNameUA());
        return updatedCategory;
    }

    @Override
    public void deleteCategory(int id) {
        categoryList.removeIf(category -> category.getId() == id);
    }
}
