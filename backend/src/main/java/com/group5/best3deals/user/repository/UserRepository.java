package com.group5.best3deals.user.repository;

import com.group5.best3deals.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    Optional<User> findByPhoneNumber(String phoneNumber);  // Added phone number search
    Optional<User> findByFirstName(String firstName);  // Optional: add first name search
    Optional<User> findByLastName(String lastName);  // Optional: add last name search
    Optional<User> findByUserType(String userType);  // Optional: add user type search

    // Corrected method for password reset token search
    Optional<User> findByResetToken(String token);
}

