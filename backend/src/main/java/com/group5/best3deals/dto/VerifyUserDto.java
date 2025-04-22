package com.group5.best3deals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Schema(description = "DTO for user verification request")
public class VerifyUserDto {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be blank")
    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @NotBlank(message = "Verification code cannot be blank")
    @Size(min = 6, max = 6, message = "Verification code must be exactly 6 characters")
    @Schema(description = "Verification code", example = "123456")
    private String verificationCode;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerifyUserDto that = (VerifyUserDto) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(verificationCode, that.verificationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, verificationCode);
    }
}