package com.group5.best3deals.unit.admin.controller;

import com.group5.best3deals.admin.controller.AdminController;
import com.group5.best3deals.user.dto.UpdateUserDto;
import com.group5.best3deals.user.entity.User;
import com.group5.best3deals.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .userType("USER")
                .build();
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        User user2 = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .password("passwrod")
                .phoneNumber("0987654321")
                .address("456 Elm St")
                .userType("USER")
                .build();

        List<User> users = Arrays.asList(user1, user2);

        when(userService.allUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = adminController.getAllUsers();

        // Assert
        assertEquals(2, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
        assertEquals("Jane", response.getBody().get(1).getFirstName());
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Johnny");
        updateUserDto.setPhoneNumber("0987654321");
        updateUserDto.setAddress("456 Elm St");

        when(userService.findUserById(user1.getId())).thenReturn(user1);
        when(userService.saveUser(user1)).thenReturn(user1);

        // Act
        ResponseEntity<User> response = adminController.updateUser(user1.getId(), updateUserDto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Johnny", response.getBody().getFirstName());
        assertEquals("0987654321", response.getBody().getPhoneNumber());
        assertEquals("456 Elm St", response.getBody().getAddress());
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        Long userId = 1L;

        // Act
        ResponseEntity<Void> response = adminController.deleteUser(userId);

        // Assert
        assertEquals(204, response.getStatusCodeValue()); // 204 No Content
        verify(userService, times(1)).deleteUser(userId);
    }
}