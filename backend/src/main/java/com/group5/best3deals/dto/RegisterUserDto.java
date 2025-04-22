package com.group5.best3deals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Schema(description = "DTO for user registration request")
public class RegisterUserDto {
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    @Schema(description = "User password", example = "Secure@123")
    private String password;

    @Schema(description = "User phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "User address", example = "123 Main St, City, Country")
    private String address;

    @Schema(description = "User type", example = "CUSTOMER")
    private String userType;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterUserDto that = (RegisterUserDto) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
