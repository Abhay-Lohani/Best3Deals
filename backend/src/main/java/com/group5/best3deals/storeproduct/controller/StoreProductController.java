package com.group5.best3deals.storeproduct.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.storeproduct.dto.request.StoreProductPutRequest;
import com.group5.best3deals.storeproduct.dto.request.StoreProductRequest;

import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.storeproduct.service.StoreProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("store-products")
@RequiredArgsConstructor
@Tag(name = "StoreProducts", description = "API to manage store products")
public class StoreProductController {

    private final StoreProductService storeProductService;

    @GetMapping("/{id}")
    @Operation(description = "get Store product by id", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreProduct>> getStoreProduct(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeProductService.getStoreProduct(id)));
    }

    @GetMapping
    @Operation(description = "get all store products ", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<StoreProduct>>> getAllStoreProducts() {
        return ResponseEntity.ok(new ApiResponse<>(true, storeProductService.getAllStoreProducts()));
    }

    @GetMapping("/nearby/best3deals")
    @Operation(description = "Fetches best 3 deals from nearby stores based on a given radius and product", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StoreProduct>> getNearbyStoreProducts(String organicApples, double latitude, double longitude, double distanceInKM) {
        return ResponseEntity.ok(storeProductService.getStoreProductsWithinDistance(organicApples, latitude, longitude, distanceInKM));
    }

    // Create a product
    @PostMapping
    @Operation(description = "Create Store products", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreProduct>> createStoreProduct(@RequestBody StoreProductRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeProductService.createStoreProduct(request)));
    }

    // Update
    @PutMapping("/{id}")
    @Operation(description = "Update a store product", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreProduct>> updateProduct(@PathVariable Long id, @RequestBody StoreProductPutRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeProductService.updateStoreProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete a store product", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Null>> deleteStoreProduct(@PathVariable Long id) {
        storeProductService.deleteAStoreProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
