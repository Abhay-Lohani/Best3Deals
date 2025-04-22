package com.group5.best3deals.wishlist.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.wishlist.dto.request.WishlistRequest;
import com.group5.best3deals.wishlist.dto.response.WishlistResponse;
import com.group5.best3deals.wishlist.entity.Wishlist;
import com.group5.best3deals.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
@Tag(name = "Wishlist", description = "API for wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    // Get my wishlist
    @GetMapping
    @Operation(
            summary = "Fetch my wishlist",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getWishlist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, wishlistService.getAllWishlistByUserId(currentUser.getId())));
    }

    @PostMapping()
    @Operation(
            summary = "Add a product to my wishlist",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(@RequestBody WishlistRequest wishlistRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>(true, wishlistService.addToWishlist(currentUser.getId(), wishlistRequest)));
    }

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Remove a product from my wishlist",
            security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Null>> deleteFromWishlist(@PathVariable Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        wishlistService.deleteFromWishlist(currentUser.getId(), productId);

        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
