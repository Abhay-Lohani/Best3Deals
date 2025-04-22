package com.group5.best3deals.unit.common;

import com.group5.best3deals.common.dto.response.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testSuccessOnlyConstructor() {
        // Test success case
        ApiResponse<?> successResponse = new ApiResponse<>(true);
        assertTrue(successResponse.isSuccess());
        assertEquals("success", successResponse.getMessage());
        assertNull(successResponse.getData());

        // Test failure case
        ApiResponse<?> errorResponse = new ApiResponse<>(false);
        assertFalse(errorResponse.isSuccess());
        assertEquals("error", errorResponse.getMessage());
        assertNull(errorResponse.getData());
    }

    @Test
    void testSuccessAndMessageConstructor() {
        ApiResponse<?> response = new ApiResponse<>(true, "Custom message");
        assertTrue(response.isSuccess());
        assertEquals("Custom message", response.getMessage());
        assertNull(response.getData());
    }



    @Test
    void testFullConstructor() {
        Integer testData = 42;
        ApiResponse<Integer> response = new ApiResponse<>(true, "Operation successful", testData);
        assertTrue(response.isSuccess());
        assertEquals("Operation successful", response.getMessage());
        assertEquals(testData, response.getData());
    }

    @Test
    void testLombokGeneratedMethods() {
        ApiResponse<String> response = new ApiResponse<>(true);

        // Test setters
        response.setSuccess(false);
        response.setMessage("Updated message");
        response.setData("New data");

        // Test getters
        assertFalse(response.isSuccess());
        assertEquals("Updated message", response.getMessage());
        assertEquals("New data", response.getData());

        // Test toString (Lombok generated)
        assertNotNull(response.toString());

        // Test equals and hashCode (Lombok generated)
        ApiResponse<String> sameResponse = new ApiResponse<>(false);
        sameResponse.setMessage("Updated message");
        sameResponse.setData("New data");

        assertEquals(response, sameResponse);
        assertEquals(response.hashCode(), sameResponse.hashCode());
    }
}