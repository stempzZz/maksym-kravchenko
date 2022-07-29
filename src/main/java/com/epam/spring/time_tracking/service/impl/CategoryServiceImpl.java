package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import com.epam.spring.time_tracking.exception.CategoryIsDefaultException;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.mapper.CategoryMapper;
import com.epam.spring.time_tracking.mapper.UpdateMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public List<CategoryForListDto> getCategories(Pageable pageable) {
        log.info("Getting categories");

        return categoryRepo.findAll(pageable).stream()
                .map(CategoryMapper.INSTANCE::toCategoryForListDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long categoryId) {
        log.info("Getting category with id: {}", categoryId);

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));
        log.info("Received category (id={}): {}", categoryId, category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto);

        if (categoryRepo.existsByNameEn(categoryDto.getNameEn()))
            throw new ExistenceException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN);
        if (categoryRepo.existsByNameUa(categoryDto.getNameUa()))
            throw new ExistenceException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_UA);

        Category category = CategoryMapper.INSTANCE.fromCategoryDto(categoryDto);

        category = categoryRepo.save(category);
        log.info("Category is created: {}", category);
        return CategoryMapper.INSTANCE.toCategoryDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Updating category (id={}): {}", categoryId, categoryDto);

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        if (category.isDefault())
            throw new CategoryIsDefaultException();
        if (categoryRepo.existsByNameEn(categoryDto.getNameEn()) && !category.getNameEn().equals(categoryDto.getNameEn()))
            throw new ExistenceException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN);
        if (categoryRepo.existsByNameUa(categoryDto.getNameUa()) && !category.getNameUa().equals(categoryDto.getNameUa()))
            throw new ExistenceException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_UA);

        category = UpdateMapper.updateCategoryWithPresentCategoryDtoFields(category, categoryDto);

        Category updatedCategory = categoryRepo.save(category);
        log.info("Category (id={}) is updated: {}", categoryId, updatedCategory);
        return CategoryMapper.INSTANCE.toCategoryDto(updatedCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category with id: {}", categoryId);

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        if (category.isDefault())
            throw new CategoryIsDefaultException();

        category.getActivities().forEach(activity -> {
            if (activity.getCategories().size() <= 1) {
                activity.getCategories().clear();
                activity.getCategories().add(categoryRepo.findByIsDefault(true)
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND)));
            } else {
                activity.getCategories().removeIf(c -> c.getId().equals(category.getId()));
            }
        });

        categoryRepo.delete(category);
        log.info("Category (id={}) is deleted", categoryId);
    }

    @Override
    public List<Category> mapCategoriesIdsToCategories(List<Long> categoriesIds) {
        Category defaultCategory = categoryRepo.findByIsDefault(true)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        if (categoriesIds == null || categoriesIds.isEmpty() ||
                (categoriesIds.size() == 1 && categoriesIds.contains(defaultCategory.getId()))) {
            List<Category> categories = new ArrayList<>();
            categories.add(defaultCategory);
            return categories;
        }
        return categoriesIds.stream()
                .filter(categoryId -> !categoryId.equals(defaultCategory.getId()))
                .map(categoryId -> categoryRepo.findById(categoryId)
                        .orElseThrow(() -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND)))
                .collect(Collectors.toList());
    }

}
