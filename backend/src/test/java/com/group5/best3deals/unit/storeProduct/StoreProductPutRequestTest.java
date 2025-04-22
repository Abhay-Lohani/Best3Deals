package com.group5.best3deals.unit.storeProduct;

import com.group5.best3deals.storeproduct.dto.request.StoreProductPutRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StoreProductPutRequestTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange
        Long storeProductId = 1L;
        Long storeId = 2L;
        Long productId = 100L;
        Long brandId = 10L;
        String storeProductUrl = "https://example.com/product";
        Double price = 99.99;
        Integer quantityInStock = 50;

        // Act
        StoreProductPutRequest request = new StoreProductPutRequest(
                storeProductId, storeId, productId, brandId, storeProductUrl, price, quantityInStock
        );

        // Assert
        assertEquals(storeProductId, request.getStoreProductId());
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
        StoreProductPutRequest request = StoreProductPutRequest.builder()
                .storeProductId(1L)
                .storeId(2L)
                .productId(100L)
                .brandId(10L)
                .storeProductUrl("https://example.com/product")
                .price(99.99)
                .quantityInStock(50)
                .build();

        // Assert
        assertEquals(1L, request.getStoreProductId());
        assertEquals(2L, request.getStoreId());
        assertEquals(100L, request.getProductId());
        assertEquals(10L, request.getBrandId());
        assertEquals("https://example.com/product", request.getStoreProductUrl());
        assertEquals(99.99, request.getPrice());
        assertEquals(50, request.getQuantityInStock());
    }

    @Test
    void testSetters() {
        // Arrange
        StoreProductPutRequest request = new StoreProductPutRequest(
                1L, 2L, 100L, 10L, "https://example.com/product", 99.99, 50
        );

        // Act
        request.setStoreProductId(3L);
        request.setStoreId(4L);
        request.setProductId(200L);
        request.setBrandId(20L);
        request.setStoreProductUrl("https://example.com/new-product");
        request.setPrice(199.99);
        request.setQuantityInStock(100);

        // Assert
        assertEquals(3L, request.getStoreProductId());
        assertEquals(4L, request.getStoreId());
        assertEquals(200L, request.getProductId());
        assertEquals(20L, request.getBrandId());
        assertEquals("https://example.com/new-product", request.getStoreProductUrl());
        assertEquals(199.99, request.getPrice());
        assertEquals(100, request.getQuantityInStock());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        StoreProductPutRequest request1 = new StoreProductPutRequest(
                1L, 2L, 100L, 10L, "https://example.com/product", 99.99, 50
        );
        StoreProductPutRequest request2 = new StoreProductPutRequest(
                1L, 2L, 100L, 10L, "https://example.com/product", 99.99, 50
        );

        // Act & Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        StoreProductPutRequest request = new StoreProductPutRequest(
                1L, 2L, 100L, 10L, "https://example.com/product", 99.99, 50
        );

        // Act
        String toStringResult = request.toString();

        // Assert
        assertTrue(toStringResult.contains("StoreProductPutRequest"));
        assertTrue(toStringResult.contains("storeProductId=1"));
        assertTrue(toStringResult.contains("storeId=2"));
        assertTrue(toStringResult.contains("productId=100"));
        assertTrue(toStringResult.contains("brandId=10"));
        assertTrue(toStringResult.contains("storeProductUrl=https://example.com/product"));
        assertTrue(toStringResult.contains("price=99.99"));
        assertTrue(toStringResult.contains("quantityInStock=50"));
    }

    @Test
    void testNullableFields() {
        // Test that optional fields can be null
        StoreProductPutRequest request = new StoreProductPutRequest(
                1L, null, null, null, null, null, null
        );

        assertNull(request.getStoreId());
        assertNull(request.getProductId());
        assertNull(request.getBrandId());
        assertNull(request.getStoreProductUrl());
        assertNull(request.getPrice());
        assertNull(request.getQuantityInStock());
    }
}