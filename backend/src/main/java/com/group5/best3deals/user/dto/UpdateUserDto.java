package com.group5.best3deals.user.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Data
public class UpdateUserDto {
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10-15 characters")
    private String phoneNumber;

    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUserDto that = (UpdateUserDto) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, phoneNumber, address);
    }
}