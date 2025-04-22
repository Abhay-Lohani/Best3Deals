package com.group5.best3deals.admin.controller;

import com.group5.best3deals.user.dto.UpdateUserDto;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Tag(name = "Admin Management", description = "APIs related to admin operations")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Fetches a list of all users. Restricted to admins.")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update user details", description = "Updates user details. Restricted to admins.")
    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UpdateUserDto updateUserDto) {
        // Fetch the user by userId
        User user = userService.findUserById(userId);

        // Update the user details from the DTO
        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPhoneNumber(updateUserDto.getPhoneNumber());
        user.setAddress(updateUserDto.getAddress());

        // Save the updated user
        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user. Restricted to admins.")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}