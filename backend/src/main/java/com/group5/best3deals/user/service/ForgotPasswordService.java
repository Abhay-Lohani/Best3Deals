package com.group5.best3deals.user.service;

import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setResetToken(UUID.randomUUID().toString());  // Generate token
            userRepository.save(user);
            // Send email logic (to be implemented later)
            return true;
        }
        return false;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);  // Corrected method
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null); // Clear token after reset
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
