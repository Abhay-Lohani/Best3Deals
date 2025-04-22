package com.group5.best3deals.unit.user.service;

import com.group5.best3deals.user.dto.UpdateUserDto;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.repository.UserRepository;
import com.group5.best3deals.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("test@example.com");
        updateUserDto.setFirstName("John");
        updateUserDto.setLastName("Doe");
        updateUserDto.setPhoneNumber("1234567890");
        updateUserDto.setAddress("123 Main St");

        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setFirstName("Jane");
        existingUser.setLastName("Doe");
        existingUser.setPhoneNumber("0987654321");
        existingUser.setAddress("456 Elm St");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User updatedUser = userService.updateUser(updateUserDto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("1234567890", updatedUser.getPhoneNumber());
        assertEquals("123 Main St", updatedUser.getAddress());

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("notfound@example.com");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(updateUserDto);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("notfound@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

}
