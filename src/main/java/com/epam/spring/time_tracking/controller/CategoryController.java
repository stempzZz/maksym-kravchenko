package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @GetMapping("/category")
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/category/{id}")
    public CategoryDto getCategoryById(@PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/category/{id}")
    public CategoryDto updateCategory(@PathVariable int id, @RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(id, categoryDto);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
