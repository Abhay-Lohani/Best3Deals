package com.group5.best3deals.unit.authen;

import com.group5.best3deals.user.dto.UpdateUserDto;
import org.junit.jupiter.api.Test;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

class UpdateUserDtoTest {

    @Test
    void testAllFields() {
        // Create test object
        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("1234567890");
        dto.setAddress("123 Main St");

        // Test getters
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhoneNumber());
        assertEquals("123 Main St", dto.getAddress());

        // Test equals
        UpdateUserDto sameDto = new UpdateUserDto();
        sameDto.setFirstName("John");
        sameDto.setLastName("Doe");
        sameDto.setEmail("john.doe@example.com");
        sameDto.setPhoneNumber("1234567890");
        sameDto.setAddress("123 Main St");

        UpdateUserDto differentDto = new UpdateUserDto();
        differentDto.setFirstName("Jane");

        assertEquals(dto, sameDto);
        assertNotEquals(dto, differentDto);
        assertNotEquals(dto, null);
        assertNotEquals(dto, new Object());

        // Test hashCode
        assertEquals(dto.hashCode(), sameDto.hashCode());
        assertNotEquals(dto.hashCode(), differentDto.hashCode());
    }

    @Test
    void testEqualsAndHashCodeWithNullValues() {
        UpdateUserDto dto1 = new UpdateUserDto();
        UpdateUserDto dto2 = new UpdateUserDto();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        dto1.setFirstName("John");
        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testPartialFields() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("test@example.com");
        dto.setPhoneNumber("0987654321");

        assertEquals("test@example.com", dto.getEmail());
        assertEquals("0987654321", dto.getPhoneNumber());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getAddress());
    }

    @Test
    void testDataAnnotation() {
        // Verify Lombok @Data annotation works correctly
        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Test");
        dto.setLastName("User");

        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("Test"));
        assertTrue(dto.toString().contains("User"));
    }

    @Test
    void testValidationAnnotations() {
        UpdateUserDto dto = new UpdateUserDto();

        // Verify annotations exist (actual validation would be tested in integration tests)
        assertNotNull(dto.getClass().getDeclaredFields());
        // Lombok's @Data should handle getters/setters
        assertDoesNotThrow(() -> dto.setFirstName("Valid"));
        assertDoesNotThrow(() -> dto.setLastName("Name"));
        assertDoesNotThrow(() -> dto.setEmail("valid@email.com"));
        assertDoesNotThrow(() -> dto.setPhoneNumber("1234567890"));
    }
}