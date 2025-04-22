package com.group5.best3deals.unit.storeProduct;

import com.group5.best3deals.storeproduct.dto.request.StoreProductRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StoreProductRequestTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        Long storeId = 1L;
        long productId = 100L;
        long brandId = 10L;
        String storeProductUrl = "https://example.com/product";
        double price = 99.99;
        int quantityInStock = 50;

        // Act
        StoreProductRequest request = new StoreProductRequest(
                storeId, productId, brandId, storeProductUrl, price, quantityInStock
        );

        // Assert
        assertEquals(storeId, request.getStoreId());
        assertEquals(productId, request.getProductId());
        assertEquals(brandId, request.getBrandId());
        assertEquals(storeProductUrl, request.getStoreProductUrl());
        assertEquals(price, request.getPrice());
        assertEquals(quantityInStock, request.getQuantityInStock());
    }

    @Test
    void testBuilder() {
        // Arrange & Act
        StoreProductRequest request = StoreProductRequest.builder()
                .storeId(1L)
                .productId(100L)
                .brandId(10L)
                .storeProductUrl("https://example.com/product")
                .price(99.99)
                .quantityInStock(50)
                .build();

        // Assert
        assertEquals(1L, request.getStoreId());
        assertEquals(100L, request.getProductId());
        assertEquals(10L, request.getBrandId());
        assertEquals("https://example.com/product", request.getStoreProductUrl());
        assertEquals(99.99, request.getPrice());
        assertEquals(50, request.getQuantityInStock());
    }

    @Test
    void testSetters() {
        // Arrange
        StoreProductRequest request = new StoreProductRequest(
                1L, 100L, 10L, "https://example.com/product", 99.99, 50
        );

        // Act
        request.setStoreId(2L);
        request.setProductId(200L);
        request.setBrandId(20L);
        request.setStoreProductUrl("https://example.com/new-product");
        request.setPrice(199.99);
        request.setQuantityInStock(100);

        // Assert
        assertEquals(2L, request.getStoreId());
        assertEquals(200L, request.getProductId());
        assertEquals(20L, request.getBrandId());
        assertEquals("https://example.com/new-product", request.getStoreProductUrl());
        assertEquals(199.99, request.getPrice());
        assertEquals(100, request.getQuantityInStock());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        StoreProductRequest request1 = new StoreProductRequest(
                1L, 100L, 10L, "https://example.com/product", 99.99, 50
        );
        StoreProductRequest request2 = new StoreProductRequest(
                1L, 100L, 10L, "https://example.com/product", 99.99, 50
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        StoreProductRequest request = new StoreProductRequest(
                1L, 100L, 10L, "https://example.com/product", 99.99, 50
        );

        // Act
        String toStringResult = request.toString();

        // Assert
        assertTrue(toStringResult.contains("StoreProductRequest"));
        assertTrue(toStringResult.contains("storeId=1"));
        assertTrue(toStringResult.contains("productId=100"));
        assertTrue(toStringResult.contains("brandId=10"));
        assertTrue(toStringResult.contains("storeProductUrl=https://example.com/product"));
        assertTrue(toStringResult.contains("price=99.99"));
        assertTrue(toStringResult.contains("quantityInStock=50"));
    }
}