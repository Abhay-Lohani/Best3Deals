package com.group5.best3deals.flyer.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.flyer.dto.request.FlyerProductPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerProductRequest;
import com.group5.best3deals.flyer.dto.request.FlyerPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerRequest;
import com.group5.best3deals.flyer.dto.response.FlyerProductResponse;
import com.group5.best3deals.flyer.dto.response.FlyerResponse;
import com.group5.best3deals.flyer.entity.Flyer;
import com.group5.best3deals.flyer.entity.FlyerProduct;
import com.group5.best3deals.flyer.service.FlyerProductService;
import com.group5.best3deals.flyer.service.FlyerService;
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
@RequestMapping("/flyers")
@Tag(name = "Flyers", description = "API for flyers")
public class FlyerController {
    private final FlyerService flyerService;
    private final FlyerProductService flyerProductService;

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Flyer>>> getAllFlyers() {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerService.getAllFlyers()));
    }

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FlyerResponse>> getFlyerById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerService.getFlyerById(id)));
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FlyerResponse>> createFlyer(@RequestBody FlyerRequest flyer) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerService.createFlyer(flyer)));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FlyerResponse>> updateFlyer(@PathVariable Long id, @RequestBody FlyerPutRequest flyer) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerService.updateFlyer(id, flyer)));
    }

    @DeleteMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFlyer(@PathVariable Long id) {
        flyerService.deleteFlyer(id);
        return ResponseEntity.ok(new ApiResponse<>(true));
    }

    @GetMapping("/store/{storeId}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Flyer>>> getFlyersByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerService.getAllFlyersByStoreId(storeId)));
    }

    // FlyerProduct APIs
    @GetMapping("/product/{productId}")
    @Operation(
            summary = "Fetch flyer product by product id",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FlyerProduct>>> getAllFlyerProductByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerProductService.getAllFlyerProductByProductId(productId)));
    }

    @PostMapping("/product")
    @Operation(
            summary = "Add flyer product into a flyer",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FlyerProductResponse>> addFlyerProduct(@RequestBody FlyerProductRequest flyerProduct) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerProductService.addFlyerProduct(flyerProduct)));
    }

    @PutMapping("/product/{id}")
    @Operation(
            summary = "Update flyer product info",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<FlyerProductResponse>> updateFlyerProduct(
            @PathVariable Long id,
            @RequestBody FlyerProductPutRequest flyerProduct) {
        return ResponseEntity.ok(new ApiResponse<>(true, flyerProductService.updateFlyerProduct(id, flyerProduct)));
    }

    @DeleteMapping("/product/{id}")
    @Operation(
            summary = "Remove a product from a flyer",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFlyerProduct(@PathVariable Long id) {
        flyerProductService.deleteFlyerProduct(id);

        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
