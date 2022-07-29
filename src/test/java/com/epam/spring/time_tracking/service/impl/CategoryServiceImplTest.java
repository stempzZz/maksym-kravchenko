package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import com.epam.spring.time_tracking.exception.CategoryIsDefaultException;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.mapper.CategoryMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.CategoryDataUtilTest;
import com.epam.spring.time_tracking.test.util.UserDataUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepo categoryRepo;

    @Test
    void getCategoriesTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        Category category1 = CategoryDataUtilTest.getCategory1();
        Category category2 = CategoryDataUtilTest.getCategory2();

        CategoryForListDto defaultCategoryDto = CategoryMapper.INSTANCE.toCategoryForListDto(defaultCategory);
        CategoryForListDto categoryDto1 = CategoryMapper.INSTANCE.toCategoryForListDto(category1);
        CategoryForListDto categoryDto2 = CategoryMapper.INSTANCE.toCategoryForListDto(category2);

        Page<Category> categories = new PageImpl<>(List.of(defaultCategory, category1, category2));
        Pageable pageable = PageRequest.of(1, 3, Sort.by("nameEn"));

        when(categoryRepo.findAll(pageable)).thenReturn(categories);
        List<CategoryForListDto> result = categoryService.getCategories(pageable);

        assertThat(result, hasSize(categories.getContent().size()));
        assertThat(result, hasItems(defaultCategoryDto, categoryDto1, categoryDto2));
    }

    @Test
    void getCategoryTest() {
        Category category = CategoryDataUtilTest.getDefaultCategory();
        CategoryDto categoryDto = CategoryMapper.INSTANCE.toCategoryDto(category);

        when(categoryRepo.findById(categoryDto.getId())).thenReturn(Optional.of(category));
        CategoryDto result = categoryService.getCategory(category.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(categoryDto.getId())),
                hasProperty("nameEn", equalTo(categoryDto.getNameEn())),
                hasProperty("nameUa", equalTo(categoryDto.getNameUa()))
        ));
    }

    @Test
    void getCategoryWithNotFoundExceptionTest() {
        Category category = CategoryDataUtilTest.getCategory1();

        when(categoryRepo.findById(category.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategory(category.getId()));
    }

    @Test
    void createCategoryTest() {
        CategoryDto categoryDto = CategoryDataUtilTest.getCategory1Dto();
        Category category = CategoryMapper.INSTANCE.fromCategoryDto(categoryDto);

        when(categoryRepo.save(any())).thenReturn(category);
        CategoryDto result = categoryService.createCategory(categoryDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(categoryDto.getId())),
                hasProperty("nameEn", equalTo(categoryDto.getNameEn())),
                hasProperty("nameUa", equalTo(categoryDto.getNameUa()))
        ));
    }

    @Test
    void createCategoryWithNameEnExistenceExceptionTest() {
        CategoryDto categoryDto = CategoryDataUtilTest.getCategory1Dto();

        when(categoryRepo.existsByNameEn(categoryDto.getNameEn())).thenReturn(true);

        assertThrows(ExistenceException.class, () -> categoryService.createCategory(categoryDto));
    }

    @Test
    void createCategoryWithNameUaExistenceExceptionTest() {
        CategoryDto categoryDto = CategoryDataUtilTest.getCategory1Dto();

        when(categoryRepo.existsByNameUa(categoryDto.getNameUa())).thenReturn(true);

        assertThrows(ExistenceException.class, () -> categoryService.createCategory(categoryDto));
    }

    @Test
    void updateCategoryTest() {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();
        Category updatedCategory = CategoryMapper.INSTANCE.fromCategoryDto(categoryDto);

        when(categoryRepo.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepo.save(any())).thenReturn(updatedCategory);
        CategoryDto result = categoryService.updateCategory(categoryDto.getId(), categoryDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(categoryDto.getId())),
                hasProperty("nameEn", equalTo(categoryDto.getNameEn())),
                hasProperty("nameUa", equalTo(categoryDto.getNameUa()))
        ));
    }

    @Test
    void updateCategoryWithNotFoundExceptionTest() {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();

        when(categoryRepo.findById(categoryDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(category.getId(), categoryDto));
    }

    @Test
    void updateCategoryWithCategoryIsDefaultExceptionTest() {
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();

        when(categoryRepo.findById(categoryDto.getId())).thenReturn(Optional.of(defaultCategory));

        assertThrows(CategoryIsDefaultException.class, () -> categoryService.updateCategory(categoryDto.getId(), categoryDto));
    }

    @Test
    void updateCategoryWithNameEnExistenceExceptionTest() {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();

        when(categoryRepo.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepo.existsByNameEn(categoryDto.getNameEn())).thenReturn(true);

        assertThrows(ExistenceException.class, () -> categoryService.updateCategory(category.getId(), categoryDto));
    }

    @Test
    void updateCategoryWithNameUaExistenceExceptionTest() {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();

        when(categoryRepo.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepo.existsByNameUa(categoryDto.getNameUa())).thenReturn(true);

        assertThrows(ExistenceException.class, () -> categoryService.updateCategory(category.getId(), categoryDto));
    }

    @Test
    void deleteCategoryTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        Category categoryForDelete = CategoryDataUtilTest.getCategory1();
        Category category2 = CategoryDataUtilTest.getCategory2();

        User admin = UserDataUtilTest.getAdmin();

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin);
        Activity activity2 = ActivityDataUtilTest.getActivity2(2, ActivityStatus.BY_ADMIN, admin);

        activity1.setCategories(new ArrayList<>(List.of(categoryForDelete)));
        activity2.setCategories(new ArrayList<>(List.of(categoryForDelete, category2)));

        categoryForDelete.setActivities(List.of(activity1, activity2));

        when(categoryRepo.findById(categoryForDelete.getId())).thenReturn(Optional.of(categoryForDelete));
        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.of(defaultCategory));
        categoryService.deleteCategory(categoryForDelete.getId());

        verify(categoryRepo, times(1)).delete(categoryForDelete);
        assertThat(categoryForDelete.getActivities(), hasItems(activity1, activity2));
        assertThat(activity1.getCategories().size(), is(1));
        assertThat(activity1.getCategories(), not(hasItem(categoryForDelete)));
        assertThat(activity1.getCategories(), hasItem(defaultCategory));
        assertThat(activity2.getCategories().size(), is(1));
        assertThat(activity2.getCategories(), not(hasItem(categoryForDelete)));
        assertThat(activity2.getCategories(), not(hasItem(defaultCategory)));
        assertThat(activity2.getCategories(), hasItem(category2));
    }

    @Test
    void deleteCategoryWithCategoryForDeleteNotFoundExceptionTest() {
        Category category = CategoryDataUtilTest.getCategory1();

        when(categoryRepo.findById(category.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(category.getId()));
    }

    @Test
    void deleteCategoryWithCategoryIsDefaultExceptionTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();

        when(categoryRepo.findById(defaultCategory.getId())).thenReturn(Optional.of(defaultCategory));

        assertThrows(CategoryIsDefaultException.class, () -> categoryService.deleteCategory(defaultCategory.getId()));
    }

    @Test
    void deleteCategoryWithDefaultCategoryNotFoundExceptionTest() {
        Category categoryForDelete = CategoryDataUtilTest.getCategory1();

        User admin = UserDataUtilTest.getAdmin();

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin);
        activity1.setCategories(new ArrayList<>(List.of(categoryForDelete)));

        categoryForDelete.setActivities(List.of(activity1));

        when(categoryRepo.findById(categoryForDelete.getId())).thenReturn(Optional.of(categoryForDelete));
        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(categoryForDelete.getId()));
    }

    @Test
    void mapCategoriesIdsToCategoriesTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        List<Category> categories = List.of(CategoryDataUtilTest.getCategory1(), CategoryDataUtilTest.getCategory2());
        List<Long> categoriesIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.of(defaultCategory));
        categories.forEach(category -> when(categoryRepo.findById(category.getId())).thenReturn(Optional.of(category)));
        List<Category> result = categoryService.mapCategoriesIdsToCategories(categoriesIds);

        assertThat(result, equalTo(categories));
    }

    @Test
    void mapCategoriesIdsToCategoriesWhereCategoriesIdsIsNullTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        List<Category> categories = List.of(defaultCategory);

        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.of(defaultCategory));
        List<Category> result = categoryService.mapCategoriesIdsToCategories(null);

        assertThat(result, equalTo(categories));
    }

    @Test
    void mapCategoriesIdsToCategoriesWhereCategoriesIdsIsEmptyTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        List<Category> categories = List.of(defaultCategory);

        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.of(defaultCategory));
        List<Category> result = categoryService.mapCategoriesIdsToCategories(List.of());

        assertThat(result, equalTo(categories));
    }

    @Test
    void mapCategoriesIdsToCategoriesWhereCategoriesIdsContainsOnlyDefaultIdTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        List<Category> categories = List.of(defaultCategory);
        List<Long> categoriesIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.of(defaultCategory));
        List<Category> result = categoryService.mapCategoriesIdsToCategories(categoriesIds);

        assertThat(result, equalTo(categories));
    }

    @Test
    void mapCategoriesIdsToCategoriesWithDefaultCategoryNotFoundExceptionTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        List<Category> categories = List.of(defaultCategory);
        List<Long> categoriesIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.mapCategoriesIdsToCategories(categoriesIds));
    }

    @Test
    void mapCategoriesIdsToCategoriesWithMappedCategoryNotFoundExceptionTest() {
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();
        List<Category> categories = List.of(CategoryDataUtilTest.getCategory1());
        List<Long> categoriesIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        when(categoryRepo.findByIsDefault(true)).thenReturn(Optional.of(defaultCategory));
        categories.forEach(category -> when(categoryRepo.findById(category.getId())).thenReturn(Optional.empty()));

        assertThrows(NotFoundException.class, () -> categoryService.mapCategoriesIdsToCategories(categoriesIds));
    }

}
