package com.group5.best3deals.unit.user;

import com.group5.best3deals.user.controller.UserController;
import com.group5.best3deals.user.dto.UpdateUserDto;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateProfile_Success() {
        // Arrange
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail("test@example.com");
        updateUserDto.setFirstName("Hasib");
        updateUserDto.setLastName("Zaman");
        updateUserDto.setPhoneNumber("1234567890");
        updateUserDto.setAddress("123 Main St");

        User updatedUser = new User();
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("Hasib");
        updatedUser.setLastName("Zaman");
        updatedUser.setPhoneNumber("1234567890");
        updatedUser.setAddress("123 Main St");

        when(userService.updateUser(updateUserDto)).thenReturn(updatedUser);

        // Act
        ResponseEntity<User> response = userController.updateProfile(updateUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Hasib", response.getBody().getFirstName());
        assertEquals("Zaman", response.getBody().getLastName());
        assertEquals("1234567890", response.getBody().getPhoneNumber());
        assertEquals("123 Main St", response.getBody().getAddress());

        verify(userService, times(1)).updateUser(updateUserDto);
    }
}