package com.group5.best3deals.controller;

import com.group5.best3deals.dto.LoginUserDto;
import com.group5.best3deals.dto.RegisterUserDto;
import com.group5.best3deals.dto.VerifyUserDto;
import com.group5.best3deals.dto.ForgotPasswordRequest;
import com.group5.best3deals.dto.ResetPasswordRequest;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.responses.LoginResponse;
import com.group5.best3deals.service.AuthenticationService;
import com.group5.best3deals.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@Tag(name = "Authentication Controller", description = "APIs for user authentication and verification")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register a new user")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    @Operation(summary = "User login and JWT generation")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify user account")
    public ResponseEntity<?> verifyUser(@Valid @RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    @Operation(summary = "Resend verification code")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Password Reset Flow
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ForgotPasswordRequest request) {
        authenticationService.requestPasswordReset(request);
        return ResponseEntity.ok("Password reset email sent");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using reset token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful");
    }
}
