package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import com.epam.spring.time_tracking.exception.CategoryIsDefaultException;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.mapper.CategoryMapper;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.service.CategoryService;
import com.epam.spring.time_tracking.test.util.CategoryDataUtilTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getCategoriesTest() throws Exception {
        CategoryForListDto defaultCategoryDto = CategoryMapper.INSTANCE.toCategoryForListDto(CategoryDataUtilTest.getDefaultCategory());
        CategoryForListDto category1Dto = CategoryMapper.INSTANCE.toCategoryForListDto(CategoryDataUtilTest.getCategory1());
        CategoryForListDto category2Dto = CategoryMapper.INSTANCE.toCategoryForListDto(CategoryDataUtilTest.getCategory2());

        List<CategoryForListDto> categories = List.of(defaultCategoryDto, category1Dto, category2Dto);

        when(categoryService.getCategories(any())).thenReturn(categories);

        mockMvc.perform(get("/api/v1/category"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(10))),
                        jsonPath("$").value(hasSize(categories.size())),
                        jsonPath("$[0].id").value(defaultCategoryDto.getId()),
                        jsonPath("$[1].id").value(category1Dto.getId()),
                        jsonPath("$[2].id").value(category2Dto.getId())
                );
    }

    @Test
    void getCategoryTest() throws Exception {
        Category category = CategoryDataUtilTest.getDefaultCategory();
        CategoryDto categoryDto = CategoryMapper.INSTANCE.toCategoryDto(category);

        when(categoryService.getCategory(category.getId())).thenReturn(categoryDto);

        mockMvc.perform(get("/api/v1/category/" + category.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(category.getId()),
                        jsonPath("$.nameEn").value(category.getNameEn()),
                        jsonPath("$.nameUa").value(category.getNameUa())
                );
    }

    @Test
    void getCategoryWithNotFoundExceptionTest() throws Exception {
        Category category = CategoryDataUtilTest.getDefaultCategory();

        when(categoryService.getCategory(category.getId())).thenThrow(new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/category/" + category.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.CATEGORY_NOT_FOUND)
                );
    }

    @Test
    void createCategoryTest() throws Exception {
        CategoryDto categoryDto = CategoryDataUtilTest.getCategory1Dto();

        when(categoryService.createCategory(any())).thenReturn(categoryDto);

        ObjectMapper jsonMapper = new ObjectMapper();

        mockMvc.perform(
                        post("/api/v1/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.nameEn").value(categoryDto.getNameEn()),
                        jsonPath("$.nameUa").value(categoryDto.getNameUa())
                );
    }

    @Test
    void createCategoryWithExistenceExceptionTest() throws Exception {
        CategoryDto categoryDto = CategoryDataUtilTest.getCategory1Dto();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(categoryService.createCategory(any())).thenThrow(new ExistenceException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN));

        mockMvc.perform(
                        post("/api/v1/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN)
                );
    }

    @Test
    void createCategoryWithMethodArgumentNotValidExceptionTest() throws Exception {
        CategoryDto categoryDto = CategoryDataUtilTest.getCategory1Dto();
        categoryDto.setNameEn("");
        categoryDto.setNameUa("");

        int validations = 2;

        ObjectMapper jsonMapper = new ObjectMapper();

        mockMvc.perform(
                        post("/api/v1/category")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void updateCategoryTest() throws Exception {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(categoryService.updateCategory(eq(category.getId()), any())).thenReturn(categoryDto);

        mockMvc.perform(
                        put("/api/v1/category/" + category.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(category.getId().intValue()),
                        jsonPath("$.nameEn").value(categoryDto.getNameEn()),
                        jsonPath("$.nameUa").value(categoryDto.getNameUa())
                );
    }

    @Test
    void updateCategoryWithNotFoundExceptionTest() throws Exception {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(categoryService.updateCategory(eq(category.getId()), any())).thenThrow(new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        mockMvc.perform(
                        put("/api/v1/category/" + category.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.CATEGORY_NOT_FOUND)
                );
    }

    @Test
    void updateCategoryWithCategoryIsDefaultExceptionTest() throws Exception {
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();
        Category defaultCategory = CategoryDataUtilTest.getDefaultCategory();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(categoryService.updateCategory(eq(defaultCategory.getId()), any())).thenThrow(new CategoryIsDefaultException());

        mockMvc.perform(
                        put("/api/v1/category/" + defaultCategory.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(notNullValue())
                );
    }

    @Test
    void updateCategoryWithExistenceExceptionTest() throws Exception {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(categoryService.updateCategory(eq(category.getId()), any())).thenThrow(new ExistenceException(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN));

        mockMvc.perform(
                        put("/api/v1/category/" + category.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.CATEGORY_EXISTS_WITH_NAME_EN)
                );
    }

    @Test
    void updateCategoryWithMethodArgumentNotValidExceptionTest() throws Exception {
        Category category = CategoryDataUtilTest.getCategory1();
        CategoryDto categoryDto = CategoryDataUtilTest.getUpdatedCategory1Dto();
        categoryDto.setNameEn("");
        categoryDto.setNameUa("");

        int validations = 2;

        ObjectMapper jsonMapper = new ObjectMapper();

        mockMvc.perform(
                        put("/api/v1/category/" + category.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(categoryDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void deleteCategoryTest() throws Exception {
        Category category = CategoryDataUtilTest.getCategory1();

        doNothing().when(categoryService).deleteCategory(category.getId());

        mockMvc.perform(delete("/api/v1/category/" + category.getId().intValue()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(category.getId());
    }

}
