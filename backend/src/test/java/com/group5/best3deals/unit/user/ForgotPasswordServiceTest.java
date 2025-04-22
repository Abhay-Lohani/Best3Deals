package com.group5.best3deals.unit.user;

import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.repository.UserRepository;
import com.group5.best3deals.user.service.ForgotPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForgotPasswordServiceTest {

    private UserRepository userRepository;
    private ForgotPasswordService forgotPasswordService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        forgotPasswordService = new ForgotPasswordService(userRepository, passwordEncoder);
    }

    @Test
    void testRequestPasswordReset_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean result = forgotPasswordService.requestPasswordReset(email);

        assertTrue(result, "Password reset request should be successful.");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testRequestPasswordReset_UserNotFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = forgotPasswordService.requestPasswordReset(email);

        assertFalse(result, "Password reset request should fail for non-existent users.");
    }

    @Test
    void testResetPassword_Success() {
        String token = "validToken";
        String newPassword = "NewPassword123";
        User user = new User();
        user.setResetToken(token);

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        boolean result = forgotPasswordService.resetPassword(token, newPassword);

        assertTrue(result, "Password reset should be successful.");
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testResetPassword_InvalidToken() {
        String token = "invalidToken";
        when(userRepository.findByResetToken(token)).thenReturn(Optional.empty());

        boolean result = forgotPasswordService.resetPassword(token, "NewPassword123");

        assertFalse(result, "Password reset should fail for invalid tokens.");
    }
}
