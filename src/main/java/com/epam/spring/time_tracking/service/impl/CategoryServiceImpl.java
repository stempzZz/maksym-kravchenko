package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.mapper.CategoryMapper;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final ActivityRepo activityRepo;

    @Override
    public List<CategoryDto> getCategories() {
        log.info("Getting categories");
        return categoryRepo.getCategories().stream()
                .map(CategoryMapper.INSTANCE::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(int categoryId) {
        log.info("Getting category with id: {}", categoryId);
        Category category = categoryRepo.getCategory(categoryId);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto);
        Category category = CategoryMapper.INSTANCE.fromCategoryDto(categoryDto);
        category = categoryRepo.createCategory(category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(int categoryId, CategoryDto categoryDto) {
        log.info("Updating category (id={}): {}", categoryId, categoryDto);
        Category category = CategoryMapper.INSTANCE.fromCategoryDto(categoryDto);
        category = categoryRepo.updateCategory(categoryId, category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(int categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        activityRepo.deleteCategoryInActivities(categoryRepo.getCategory(categoryId));
        categoryRepo.deleteCategory(categoryId);
    }
}
