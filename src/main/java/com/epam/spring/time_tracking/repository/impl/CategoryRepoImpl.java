package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.lang.Language;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryRepoImpl implements CategoryRepo {

    private final List<Category> categoryList = new ArrayList<>();
    private int idCounter;

    {
        categoryList.add(Category.builder().nameEN("Other").nameUA("Інше").build());
    }

    @Override
    public List<Category> getCategories() {
        log.info("Getting categories");
        return categoryList;
    }

    @Override
    public Category getCategory(int categoryId) {
        log.info("Getting category with id: {}", categoryId);
        return categoryList.stream()
                .filter(category -> category.getId() == categoryId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("category is not found"));
    }

    @Override
    public Category createCategory(Category category) {
        log.info("Creating category: {}", category);
        if (!checkForUnique(category, Language.EN))
            throw new RuntimeException("category name (EN) already exists");
        if (!checkForUnique(category, Language.UA))
            throw new RuntimeException("category name (UA) already exists");
        category.setId(++idCounter);
        categoryList.add(category);
        return category;
    }

    @Override
    public Category updateCategory(int categoryId, Category category) {
        log.info("Updating category (id={}): {}", categoryId, category);
        if (!checkForUnique(category, Language.EN))
            throw new RuntimeException("category name (EN) already exists");
        if (!checkForUnique(category, Language.UA))
            throw new RuntimeException("category name (UA) already exists");
        Category updatedCategory = getCategory(categoryId);
        updatedCategory.setNameEN(category.getNameEN());
        updatedCategory.setNameUA(category.getNameUA());
        return updatedCategory;
    }

    @Override
    public void deleteCategory(int categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        categoryList.removeIf(category -> category.getId() == categoryId);
    }

    private boolean checkForUnique(Category category, Language language) {
        log.info("Checking category for unique");
        if (language.equals(Language.UA))
            return categoryList.stream()
                    .noneMatch(c -> c.getNameUA().equals(category.getNameUA()));
        return categoryList.stream()
                .noneMatch(c -> c.getNameEN().equals(category.getNameEN()));
    }

}
