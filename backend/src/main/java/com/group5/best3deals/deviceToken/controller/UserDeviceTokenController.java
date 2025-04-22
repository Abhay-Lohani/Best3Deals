package com.group5.best3deals.deviceToken.controller;

import com.group5.best3deals.common.dto.response.ApiResponse;
import com.group5.best3deals.deviceToken.dto.request.DeviceTokenRequest;
import com.group5.best3deals.deviceToken.dto.response.DeviceTokenResponse;
import com.group5.best3deals.deviceToken.entity.UserDeviceToken;
import com.group5.best3deals.deviceToken.service.UserDeviceTokenService;
import com.group5.best3deals.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/deviceToken")
@Tag(name = "UserDeviceToken", description = "API for firebase token storage")
public class UserDeviceTokenController {

    private final UserDeviceTokenService userDeviceTokenService;

    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<UserDeviceToken>>> getUserDeviceToken() {
        return ResponseEntity.ok(new ApiResponse<>(true, userDeviceTokenService.getAllDeviceTokens()));
    }

    @GetMapping("/my")
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDeviceToken>> getUserDeviceTokenByUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                new ApiResponse<>(true, userDeviceTokenService.getDeviceTokenByUserId(user.getId())));
    }

    @PostMapping
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DeviceTokenResponse>> storeUserDeviceToken(
            @RequestBody DeviceTokenRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                new ApiResponse<>(true, userDeviceTokenService.saveDeviceToken(request, user.getId())));
    }

    @DeleteMapping()
    @Operation(security = @SecurityRequirement(name = "BearerAuth"))
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteUserDeviceToken() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        userDeviceTokenService.deleteDeviceTokenByUserId(user.getId());

        return ResponseEntity.ok(new ApiResponse<>(true));
    }
}
