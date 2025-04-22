package com.group5.best3deals.unit.user.entity;

import com.group5.best3deals.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .userType("ADMIN")
                .build();
    }

    @Test
    public void testGetAuthorities() {
        // Act
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Assert
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    public void testIsAccountNonExpired() {
        // Act & Assert
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    public void testIsAccountNonLocked() {
        // Act & Assert
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    public void testIsCredentialsNonExpired() {
        // Act & Assert
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    public void testIsEnabled() {
        user.setEnabled(true);

        // Act & Assert
        assertTrue(user.isEnabled());
    }
}