package com.group5.best3deals.product.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.product.dto.request.ProductPutRequest;
import com.group5.best3deals.product.dto.request.ProductRequest;
import com.group5.best3deals.product.dto.response.ProductResponse;
import com.group5.best3deals.product.entity.Product;
import com.group5.best3deals.storeproduct.entity.StoreProduct;
import com.group5.best3deals.product.service.ProductService;
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
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API for products")
public class ProductController {
    private final ProductService productService;

    // Fetch all products
    @GetMapping()
    @Operation(description = "Fetch all products", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Product>>> getProducts() {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getAllProducts()));
    }

    // Fetch a product with ID
    @GetMapping("/{id}")
    @Operation(description = "Fetch a product with ID", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getProductById(id)));
    }

    // Create a product
    @PostMapping()
    @Operation(description = "Create a product", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest product) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.createProduct(product)));
    }

    // Update a product
    @PutMapping("/{id}")
    @Operation(description = "Update a product", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody ProductPutRequest product) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.updateProduct(id, product)));
    }

    // Delete a product
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete a product", security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Null>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>(true));
    }

    // Get products by category id
    @GetMapping("/category/{categoryId}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getAllProductsByCategoryId(categoryId)));
    }

    // Get products by store id
    @GetMapping("/store/{storeId}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<StoreProduct>>> getProductsWithinRadius(@PathVariable Long storeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getAllProductsByStoreId(storeId)));
    }

    // Get products by location within radius
    @GetMapping("/{latitude}/{longitude}/{radius}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<StoreProduct>>> getProductsWithinRadius(
            @PathVariable Double latitude, @PathVariable Double longitude, @PathVariable Integer radius) {
        return ResponseEntity.ok(new ApiResponse<>(true, productService.getAllProductsWithinRadius(latitude, longitude, radius)));
    }
}
