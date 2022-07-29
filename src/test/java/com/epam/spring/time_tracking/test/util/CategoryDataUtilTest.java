package com.epam.spring.time_tracking.test.util;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.model.Category;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDataUtilTest {

    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_NAME_EN = "Other";
    public static final String DEFAULT_NAME_UA = "Інше";
    public static final boolean IS_DEFAULT = true;

    public static final Long CATEGORY_1_ID = 2L;
    public static final String CATEGORY_1_NAME_EN = "Category 1";
    public static final String CATEGORY_1_NAME_UA = "Категорія 1";

    public static final Long CATEGORY_2_ID = 3L;
    public static final String CATEGORY_2_NAME_EN = "Category 2";
    public static final String CATEGORY_2_NAME_UA = "Категорія 2";

    public static final String UPDATE_NAME_EN = "Activity 1111";
    public static final String UPDATE_NAME_UA = "Категорія 1111";

    public static Category getDefaultCategory() {
        Category defaultCategory = new Category();
        defaultCategory.setId(DEFAULT_ID);
        defaultCategory.setNameEn(DEFAULT_NAME_EN);
        defaultCategory.setNameUa(DEFAULT_NAME_UA);
        defaultCategory.setDefault(IS_DEFAULT);
        return defaultCategory;
    }

    public static Category getCategory1() {
        Category category = new Category();
        category.setId(CATEGORY_1_ID);
        category.setNameEn(CATEGORY_1_NAME_EN);
        category.setNameUa(CATEGORY_1_NAME_UA);
        return category;
    }

    public static Category getCategory2() {
        Category category = new Category();
        category.setId(CATEGORY_2_ID);
        category.setNameEn(CATEGORY_2_NAME_EN);
        category.setNameUa(CATEGORY_2_NAME_UA);
        return category;
    }

    public static CategoryDto getDefaultCategoryDto() {
        CategoryDto defaultCategoryDto = new CategoryDto();
        defaultCategoryDto.setId(DEFAULT_ID);
        defaultCategoryDto.setNameEn(DEFAULT_NAME_EN);
        defaultCategoryDto.setNameUa(DEFAULT_NAME_UA);
        return defaultCategoryDto;
    }

    public static CategoryDto getCategory1Dto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(CATEGORY_1_ID);
        categoryDto.setNameEn(CATEGORY_1_NAME_EN);
        categoryDto.setNameUa(CATEGORY_1_NAME_UA);
        return categoryDto;
    }

    public static CategoryDto getUpdatedCategory1Dto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(CATEGORY_1_ID);
        categoryDto.setNameEn(UPDATE_NAME_EN);
        categoryDto.setNameUa(UPDATE_NAME_UA);
        return categoryDto;
    }

}
