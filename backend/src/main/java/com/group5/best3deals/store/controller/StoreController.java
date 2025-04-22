package com.group5.best3deals.store.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.store.dto.request.StorePutRequest;
import com.group5.best3deals.store.dto.request.StoreRequest;
import com.group5.best3deals.store.dto.response.StoreResponse;
import com.group5.best3deals.store.entity.Store;
import com.group5.best3deals.store.service.StoreService;
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
@RequiredArgsConstructor
@RequestMapping("/stores")
@Tag(name = "Stores", description = "API for stores")
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Store>> getStoreById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeService.getStoreById(id)));
    }

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        return ResponseEntity.ok(new ApiResponse<>(true, storeService.getAllStores()));
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreResponse>> addStore(@RequestBody StoreRequest store) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeService.createStore(store)));
    }

    @PutMapping("/{id}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<StoreResponse>> updateStore(
            @PathVariable("id") Long id, @RequestBody StorePutRequest store) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeService.updateStore(id, store)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Null>> deleteStore(@PathVariable("id") Long id) {
        storeService.deleteStoreById(id);

        return ResponseEntity.ok(new ApiResponse<>(true));
    }

    @GetMapping("/all/{latitude}/{longitude}/{radius}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Store>>> getStoresWithinRadius(
            @PathVariable Double latitude, @PathVariable Double longitude, @PathVariable Integer radius) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeService.getStoresWithinRadius(latitude, longitude, radius)));
    }

    @GetMapping("/brand/{brandId}")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Store>>> getStoresWithinBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(new ApiResponse<>(true, storeService.getStoresByBrandId(brandId)));
    }
}
