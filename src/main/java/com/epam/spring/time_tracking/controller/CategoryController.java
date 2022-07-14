package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.api.CategoryApi;
import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    public List<CategoryForListDto> getCategories(Pageable pageable) {
        log.info("Getting categories");
        return categoryService.getCategories(pageable);
    }

    @Override
    public CategoryDto getCategory(Long categoryId) {
        log.info("Getting category with id: {}", categoryId);
        return categoryService.getCategory(categoryId);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Updating category (id={}): {}", categoryId, categoryDto);
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Long categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
