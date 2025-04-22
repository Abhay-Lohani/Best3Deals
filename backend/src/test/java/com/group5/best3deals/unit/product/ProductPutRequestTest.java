package com.group5.best3deals.unit.product;

import com.group5.best3deals.product.dto.request.ProductPutRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductPutRequestTest {

    @Test
    public void testProductPutRequest() {
        ProductPutRequest request = new ProductPutRequest();
        request.setName("Test");
        request.setDescription("Description");
        request.setImgUrl("image.jpg");
        request.setPrice(9.99);
        request.setQuantityInStock(10);
        request.setCategoryId(1L);
        request.setStoreId(2L);

        assertEquals("Test", request.getName());
        assertEquals("Description", request.getDescription());
        assertEquals("image.jpg", request.getImgUrl());
        assertEquals(9.99, request.getPrice());
        assertEquals(10, request.getQuantityInStock());
        assertEquals(1L, request.getCategoryId());
        assertEquals(2L, request.getStoreId());
    }
}
