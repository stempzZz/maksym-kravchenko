package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<CategoryDto> getCategories() {
        log.info("Getting categories");
        return categoryService.getCategories();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{categoryId}")
    public CategoryDto getCategory(@PathVariable int categoryId) {
        log.info("Getting category with id: {}", categoryId);
        return categoryService.getCategory(categoryId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Creating category: {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{categoryId}")
    public CategoryDto updateCategory(@PathVariable int categoryId, @RequestBody CategoryDto categoryDto) {
        log.info("Updating category (id={}): {}", categoryId, categoryDto);
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int categoryId) {
        log.info("Deleting category with id: {}", categoryId);
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
