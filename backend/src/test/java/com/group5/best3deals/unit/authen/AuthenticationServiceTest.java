package com.group5.best3deals.unit.authen;

import com.group5.best3deals.dto.*;
import com.group5.best3deals.exception.UserNotFoundException;
import com.group5.best3deals.service.AuthenticationService;
import com.group5.best3deals.service.EmailService;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterUserDto registerUserDto;
    private LoginUserDto loginUserDto;
    private VerifyUserDto verifyUserDto;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerUserDto = new RegisterUserDto();
        registerUserDto.setPhoneNumber("1234567890");
        registerUserDto.setUserType("CUSTOMER");
        registerUserDto.setAddress("123 Main St");
        registerUserDto.setEmail("john.doe@example.com");
        registerUserDto.setPassword("password123");
        registerUserDto.setLastName("John");
        registerUserDto.setFirstName("Doe");

        loginUserDto = new LoginUserDto();
loginUserDto.setPassword("password123");
loginUserDto.setEmail("john.doe@example.com");

        verifyUserDto = new VerifyUserDto();
        verifyUserDto.setEmail("john.doe@example.com");
        verifyUserDto.setVerificationCode("123456");

        forgotPasswordRequest = new ForgotPasswordRequest();
forgotPasswordRequest.setEmail("john.doe@example.com");
        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken(UUID.randomUUID().toString());
        resetPasswordRequest.setNewPassword(  "newPassword123");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setPhoneNumber("1234567890");
        user.setAddress("123 Main St");
        user.setUserType("CUSTOMER");
        user.setDateJoined(LocalDateTime.now());
        user.setVerificationCode("123456");
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
    }

    @Test
    void signup_ShouldRegisterNewUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authenticationService.signup(registerUserDto);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void signup_ShouldDeleteUnverifiedUserAndRegisterNew() {
        User unverifiedUser = new User();
        unverifiedUser.setEmail("john.doe@example.com");
        unverifiedUser.setEnabled(false);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(unverifiedUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authenticationService.signup(registerUserDto);

        assertNotNull(result);
        verify(userRepository, times(1)).delete(unverifiedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signup_ShouldThrowWhenEmailInUse() {
        User existingUser = new User();
        existingUser.setEmail("john.doe@example.com");
        existingUser.setEnabled(true);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        Exception exception = assertThrows(RuntimeException.class,
                () -> authenticationService.signup(registerUserDto));

        assertEquals("Email is already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticate_ShouldReturnUserWhenCredentialsValid() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        user.setEnabled(true);

        User result = authenticationService.authenticate(loginUserDto);

        assertNotNull(result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_ShouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(loginUserDto));
    }

    @Test
    void authenticate_ShouldThrowWhenAccountNotVerified() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginUserDto));
    }

    @Test
    void authenticate_ShouldThrowWhenInvalidCredentials() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        user.setEnabled(true);

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginUserDto));
    }

    @Test
    void authenticate_ShouldThrowWhenAuthenticationFails() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        user.setEnabled(true);
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginUserDto));
    }

    @Test
    void verifyUser_ShouldEnableUserWhenCodeValid() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        authenticationService.verifyUser(verifyUserDto);

        assertTrue(user.isEnabled());
        assertNull(user.getVerificationCode());
        assertNull(user.getVerificationCodeExpiresAt());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void verifyUser_ShouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.verifyUser(verifyUserDto));
    }

    @Test
    void verifyUser_ShouldThrowWhenCodeExpired() {
        user.setVerificationCodeExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authenticationService.verifyUser(verifyUserDto));
    }

    @Test
    void verifyUser_ShouldThrowWhenCodeInvalid() {
        verifyUserDto.setVerificationCode("654321");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authenticationService.verifyUser(verifyUserDto));
    }

    @Test
    void resendVerificationCode_ShouldUpdateCodeAndSendEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        authenticationService.resendVerificationCode(user.getEmail());

        assertNotNull(user.getVerificationCode());
        assertNotNull(user.getVerificationCodeExpiresAt());
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resendVerificationCode_ShouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authenticationService.resendVerificationCode("nonexistent@example.com"));
    }

    @Test
    void resendVerificationCode_ShouldThrowWhenAlreadyVerified() {
        user.setEnabled(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authenticationService.resendVerificationCode(user.getEmail()));
    }

    @Test
    void requestPasswordReset_ShouldGenerateTokenAndSendEmail() {
        user.setEnabled(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        authenticationService.requestPasswordReset(forgotPasswordRequest);

        assertNotNull(user.getResetToken());
        assertNotNull(user.getPasswordResetExpiry());
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void requestPasswordReset_ShouldDoNothingWhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        authenticationService.requestPasswordReset(forgotPasswordRequest);

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void resetPassword_ShouldUpdatePasswordWhenTokenValid() {
        user.setResetToken(resetPasswordRequest.getToken());
        user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(15));
        when(userRepository.findByResetToken(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        authenticationService.resetPassword(resetPasswordRequest);

        assertEquals("newEncodedPassword", user.getPassword());
        assertNull(user.getResetToken());
        assertNull(user.getPasswordResetExpiry());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void resetPassword_ShouldThrowWhenTokenInvalid() {
        when(userRepository.findByResetToken(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.resetPassword(resetPasswordRequest));
    }

    @Test
    void resetPassword_ShouldThrowWhenTokenExpired() {
        user.setResetToken(resetPasswordRequest.getToken());
        user.setPasswordResetExpiry(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByResetToken(anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authenticationService.resetPassword(resetPasswordRequest));
    }


}