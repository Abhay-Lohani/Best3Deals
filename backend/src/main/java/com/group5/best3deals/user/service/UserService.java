package com.group5.best3deals.user.service;

import com.group5.best3deals.user.dto.UpdateUserDto;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Method to fetch all users
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    // Method to find a user by email
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // New method to update user details
    public User updateUser(UpdateUserDto updateUserDto) {
        User user = userRepository.findByEmail(updateUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setPhoneNumber(updateUserDto.getPhoneNumber());
        user.setAddress(updateUserDto.getAddress());

        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    // Method to save a user to the database
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
