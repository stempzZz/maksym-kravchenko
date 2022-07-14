package com.epam.spring.time_tracking.api;

import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.category.CategoryForListDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Category management API")
@RequestMapping("/api/v1/category")
public interface CategoryApi {

    @ApiOperation("Get all categories")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<CategoryForListDto> getCategories(@PageableDefault(sort = "nameEn") Pageable pageable);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", paramType = "path", required = true, value = "Category id")
    })
    @ApiOperation("Get category")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{categoryId}")
    CategoryDto getCategory(@PathVariable Long categoryId);

    @ApiOperation("Create category")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", paramType = "path", required = true, value = "Category id")
    })
    @ApiOperation("Update category")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{categoryId}")
    CategoryDto updateCategory(@PathVariable Long categoryId, @RequestBody @Valid CategoryDto categoryDto);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", paramType = "path", required = true, value = "Category id")
    })
    @ApiOperation("Delete category")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{categoryId}")
    ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId);

}
