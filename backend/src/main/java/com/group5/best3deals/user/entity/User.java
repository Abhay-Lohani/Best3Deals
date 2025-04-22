package com.group5.best3deals.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    private boolean enabled;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String userType; // E.g. "ADMIN", "USER", etc.

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime dateJoined;

    // New fields for password reset functionality
    @Column(nullable = true)
    private String resetToken;  // Changed from passwordResetToken to resetToken

    @Column(nullable = true)
    private LocalDateTime passwordResetExpiry;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userType)); // Prefix with "ROLE_" for Spring Security
    }

    @Override
    public String getUsername() {
        return email;  // You could choose to return the email instead of leaving it empty
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Setter Getter for resetToken
    public void setResetToken(String token) {
        this.resetToken = token;
    }

    public String getResetToken() {
        return resetToken;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id); // Only compare by ID
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); // Consistent hash code
    }
}
