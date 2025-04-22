package com.group5.best3deals.unit.product;

import com.group5.best3deals.product.dto.request.ProductRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductRequestTest {

    @Test
    public void testProductRequestBuilder() {
        ProductRequest request = new ProductRequest
                ("Test Product",
             "Test Description",
               "test.jpg",
                1L);


        assertEquals("Test Product", request.getName());
        assertEquals("Test Description", request.getDescription());
        assertEquals("test.jpg", request.getImgUrl());
        assertEquals(1L, request.getCategoryId());
    }
}