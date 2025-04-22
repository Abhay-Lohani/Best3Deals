package com.group5.best3deals.unit.authen;

import com.group5.best3deals.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long EXPIRATION = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        // Use ReflectionTestUtils to set private fields
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    @Test
    void extractEmail_ShouldReturnEmailFromToken() {
        String token = jwtService.generateToken(new TestUserDetails("test@example.com", "password"));
        String email = jwtService.extractEmail(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(new TestUserDetails("test@example.com", "password"));

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // Valid JWT has 3 parts
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        TestUserDetails userDetails = new TestUserDetails("test@example.com", "password");
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidUser() {
        TestUserDetails userDetails = new TestUserDetails("test@example.com", "password");
        String token = jwtService.generateToken(userDetails);
        TestUserDetails otherUser = new TestUserDetails("other@example.com", "password");
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

//    @Test
//    void isTokenValid_ShouldReturnFalseForExpiredToken() {
//        // Temporarily set very short expiration
//        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
//
//        TestUserDetails userDetails = new TestUserDetails("test@example.com", "password");
//        String token = jwtService.generateToken(userDetails);
//
//        // Wait for token to expire
//        try { Thread.sleep(2); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
//
//        assertFalse(jwtService.isTokenValid(token, userDetails));
//
//        // Reset expiration
//        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
//    }

    // Simple test UserDetails implementation
    private static class TestUserDetails implements org.springframework.security.core.userdetails.UserDetails {
        private final String username;
        private final String password;

        public TestUserDetails(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override public String getUsername() { return username; }
        @Override public String getPassword() { return password; }
        @Override public List getAuthorities() { return Collections.emptyList(); }
        @Override public boolean isAccountNonExpired() { return true; }
        @Override public boolean isAccountNonLocked() { return true; }
        @Override public boolean isCredentialsNonExpired() { return true; }
        @Override public boolean isEnabled() { return true; }
    }
}