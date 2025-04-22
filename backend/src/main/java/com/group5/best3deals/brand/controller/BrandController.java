package com.group5.best3deals.brand.controller;

import com.group5.best3deals.brand.dto.request.BrandPutRequest;
import com.group5.best3deals.brand.dto.request.BrandRequest;
import com.group5.best3deals.brand.dto.response.BrandResponse;
import com.group5.best3deals.brand.entity.Brand;
import com.group5.best3deals.brand.service.BrandService;
import com.group5.best3deals.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
@Tag(name = "Brands", description = "API for brands")
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        return ResponseEntity.ok(new ApiResponse<>(true, brandService.getAllBrands()));
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Brand>> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, brandService.getBrandById(id)));
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@RequestBody BrandRequest brand) {
        return ResponseEntity.ok(new ApiResponse<>(true, brandService.createBrand(brand)));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BrandResponse>> updateFlyer(@PathVariable Long id, @RequestBody BrandPutRequest brand) {
        return ResponseEntity.ok(new ApiResponse<>(true, brandService.updateBrand(id, brand)));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
