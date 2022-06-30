package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.repository.CategoryRepo;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        category = categoryRepo.createCategory(category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getCategories() {
        return categoryRepo.getCategories().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(int id) {
        Category category = categoryRepo.getCategoryById(id);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(int id, CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        category = categoryRepo.updateCategory(id, category);
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public void deleteCategory(int id) {
        categoryRepo.deleteCategory(id);
    }
}
