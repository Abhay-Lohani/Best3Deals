package com.group5.best3deals.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Schema(description = "DTO for user login request")
public class LoginUserDto {
    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User password", example = "Password@123")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginUserDto that = (LoginUserDto) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}