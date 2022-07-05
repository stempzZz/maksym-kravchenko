package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.exception.ExistingException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.model.enums.Language;
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
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));
    }

    @Override
    public Category createCategory(Category category) {
        log.info("Creating category: {}", category);
        if (!checkForUnique(category, Language.EN))
            throw new ExistingException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN);
        if (!checkForUnique(category, Language.UA))
            throw new ExistingException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_UA);
        category.setId(++idCounter);
        categoryList.add(category);
        return category;
    }

    @Override
    public Category updateCategory(int categoryId, Category category) {
        log.info("Updating category (id={}): {}", categoryId, category);
        Category updatedCategory = getCategory(categoryId);
        if (!category.getNameEN().equals(updatedCategory.getNameEN()) && !checkForUnique(category, Language.EN))
            throw new ExistingException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN);
        if (!category.getNameUA().equals(updatedCategory.getNameUA()) && !checkForUnique(category, Language.UA))
            throw new ExistingException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_UA);
        updatedCategory.setNameEN(category.getNameEN());
        updatedCategory.setNameUA(category.getNameUA());
        return updatedCategory;
    }

    @Override
    public void deleteCategory(int categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        categoryList.removeIf(category -> categoryId != 0 && category.getId() == categoryId);
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
