package com.group5.best3deals.service;

import com.group5.best3deals.dto.LoginUserDto;
import com.group5.best3deals.dto.RegisterUserDto;
import com.group5.best3deals.dto.VerifyUserDto;
import com.group5.best3deals.dto.ForgotPasswordRequest;
import com.group5.best3deals.dto.ResetPasswordRequest;
import com.group5.best3deals.exception.UserNotFoundException;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Integer EXPIRES_IN = 15;  // 15 minutes for verification

    private static final int VERIFICATION_CODE_MIN = 100000;
    private static final int VERIFICATION_CODE_MAX = 999999;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public User signup(RegisterUserDto input) {
        Optional<User> existingUser = userRepository.findByEmail(input.getEmail());

        if (existingUser.isPresent()) {
            if (!existingUser.get().isEnabled()) {
                userRepository.delete(existingUser.get());
                userRepository.flush(); // Ensure deletion is committed before inserting a new record
            } else {
                throw new RuntimeException("Email is already in use");
            }
        }

        // Proceed with new user registration
        User user = new User();
        user.setFirstName(input.getFirstName());
        user.setLastName(input.getLastName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setPhoneNumber(input.getPhoneNumber());
        user.setAddress(input.getAddress());
        user.setUserType(input.getUserType());
        user.setDateJoined(LocalDateTime.now());

        // Generate verification code
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(EXPIRES_IN));
        user.setEnabled(false);

        sendVerificationEmail(user);
        return userRepository.save(user);
    }



    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }

        // Verifying the password using passwordEncoder
        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(VerifyUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired");
        }

        if (!user.getVerificationCode().equals(input.getVerificationCode())) {
            throw new RuntimeException("Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    public void resendVerificationCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(EXPIRES_IN));
        sendVerificationEmail(user);
        userRepository.save(user);
    }

    // Password reset methods
    public void requestPasswordReset(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setPasswordResetExpiry(LocalDateTime.now().plusMinutes(EXPIRES_IN)); // Token valid for 15 minutes
            userRepository.save(user);

            // Send email with the token
            String webLink = "http://172.17.3.115:8080/reset-password.html?token=" + token;
            String appLink = "myapp://reset-password?token=" + token;

            String htmlMessage = "<html>"
                    + "<body style=\"font-family: 'Helvetica Neue', sans-serif; background-color: #e0f7fa; padding: 20px;\">"
                    + "<div style=\"max-width: 500px; background-color: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1); text-align: center;\">"
                    + "<h2 style=\"color: #00695c;\">Reset Your Password</h2>"
                    + "<p style=\"font-size: 16px; color: #004d40;\">Click the button below to reset your password. You can do this via the web browser or the mobile app.</p>"
                    + "<a href=\"" + webLink + "\" style=\"display: inline-block; text-decoration: none; background-color: #00695c; color: white; padding: 12px 20px; border-radius: 5px; font-size: 16px; margin: 10px;\"> Reset via Web</a>"
                    + "<a href=\"" + appLink + "\" style=\"display: inline-block; text-decoration: none; background-color: #d32f2f; color: white; padding: 12px 20px; border-radius: 5px; font-size: 16px; margin: 10px;\"> Reset via App</a>"
                    + "<p style=\"margin-top: 20px; font-size: 14px; color: #666;\">If you did not request this, please ignore this email.</p>"
                    + "</div></body></html>";
            emailService.sendEmail(user.getEmail(), "Password Reset Request", htmlMessage);
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByResetToken(request.getToken());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid or expired reset token");
        }

        User user = userOptional.get();
        if (user.getPasswordResetExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setPasswordResetExpiry(null);
        userRepository.save(user);
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: 'Helvetica Neue', sans-serif;\">"
                + "<div style=\"background-color: #e0f7fa; padding: 20px;\">"
                + "<h2 style=\"color: #00695c;\">Welcome to Best3Deals!</h2>"
                + "<p style=\"font-size: 16px; color: #004d40;\">Please use the verification code below to complete your registration:</p>"
                + "<div style=\"background-color: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #004d40;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #d32f2f;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    /**
     * Generates a 6 digit verification code
     */
    private String generateVerificationCode() {
        return String.valueOf(new Random()
                .nextInt(VERIFICATION_CODE_MAX - VERIFICATION_CODE_MIN + 1) + VERIFICATION_CODE_MIN);
    }
}
