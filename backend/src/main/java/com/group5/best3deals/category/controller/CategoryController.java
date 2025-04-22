package com.group5.best3deals.category.controller;

import com.group5.best3deals.category.entity.Category;
import com.group5.best3deals.category.service.CategoryService;
import com.group5.best3deals.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Category", description = "API for category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(description = "Fetch all categories", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        return ResponseEntity.ok(new ApiResponse<>(true, categoryService.getAllCategories()));
    }

    @GetMapping("/{id}")
    @Operation(description = "Fetch a product with ID", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, categoryService.getCategoryById(id)));
    }
}
