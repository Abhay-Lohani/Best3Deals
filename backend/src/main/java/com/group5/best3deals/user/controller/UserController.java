package com.group5.best3deals.user.controller;

import com.group5.best3deals.user.dto.UpdateUserDto;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Management", description = "APIs related to user operations")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Endpoint to fetch the currently authenticated user
    @Operation(summary = "Get authenticated user", description = "Fetches details of the currently logged-in user.")
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    // Endpoint to fetch all users (may be restricted based on authorization)
    @Operation(summary = "Get all users", description = "Fetches a list of all users. Restricted to admins.")
    @GetMapping("/all")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    // Endpoint to update user profile
    @PutMapping("/update-profile")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UpdateUserDto updateUserDto) {
        User updatedUser = userService.updateUser(updateUserDto);
        return ResponseEntity.ok(updatedUser); // Return the updated user with a 200 OK status
    }
}
