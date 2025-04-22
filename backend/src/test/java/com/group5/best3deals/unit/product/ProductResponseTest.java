package com.group5.best3deals.unit.product;

import com.group5.best3deals.product.dto.response.ProductResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductResponseTest {

    @Test
    public void testProductResponse() {
        ProductResponse response = new ProductResponse(1L, "Test", "Description", "image.jpg", 1L);

        assertEquals(1L, response.getId());
        assertEquals("Test", response.getName());
        assertEquals("Description", response.getDescription());
        assertEquals("image.jpg", response.getImgUrl());
        assertEquals(1L, response.getCategoryId());
    }
}