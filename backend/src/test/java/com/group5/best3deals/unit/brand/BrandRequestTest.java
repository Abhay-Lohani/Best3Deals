package com.group5.best3deals.brand.dto.request;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class BrandRequestTest {

    // Tests for BrandPutRequest
    @Test
    void testBrandPutRequestGettersAndSetters() {
        BrandPutRequest request = new BrandPutRequest();
        request.setName("Test Brand");
        request.setImageUrl("brand.jpg");

        assertEquals("Test Brand", request.getName());
        assertEquals("brand.jpg", request.getImageUrl());
    }

    @Test
    void testBrandPutRequestEqualsAndHashCode() {
        BrandPutRequest request1 = new BrandPutRequest();
        request1.setName("Brand A");
        request1.setImageUrl("image1.jpg");

        BrandPutRequest request2 = new BrandPutRequest();
        request2.setName("Brand A");
        request2.setImageUrl("image1.jpg");

        BrandPutRequest differentRequest = new BrandPutRequest();
        differentRequest.setName("Brand B");
        differentRequest.setImageUrl("image1.jpg");

        // Test equals
        assertEquals(request1, request2);
        assertNotEquals(request1, differentRequest);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        // Test hashCode
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), differentRequest.hashCode());
    }

    @Test
    void testBrandPutRequestNullFields() {
        BrandPutRequest nullRequest1 = new BrandPutRequest();
        BrandPutRequest nullRequest2 = new BrandPutRequest();

        assertEquals(nullRequest1, nullRequest2);
        assertEquals(nullRequest1.hashCode(), nullRequest2.hashCode());
    }

    // Tests for BrandRequest
    @Test
    void testBrandRequestConstructorAndGetters() {
        BrandRequest request = new BrandRequest("Test Brand", "brand.jpg");

        assertEquals("Test Brand", request.getName());
        assertEquals("brand.jpg", request.getImageUrl());
    }

    @Test
    void testBrandRequestEqualsAndHashCode() {
        BrandRequest request1 = new BrandRequest("Brand A", "image1.jpg");
        BrandRequest request2 = new BrandRequest("Brand A", "image1.jpg");
        BrandRequest differentRequest = new BrandRequest("Brand B", "image1.jpg");

        // Test equals
        assertEquals(request1, request2);
        assertNotEquals(request1, differentRequest);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        // Test hashCode
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), differentRequest.hashCode());
    }

    @Test
    void testBrandRequestNullFields() {
        BrandRequest nullRequest1 = new BrandRequest(null, null);
        BrandRequest nullRequest2 = new BrandRequest(null, null);

        assertEquals(nullRequest1, nullRequest2);
        assertEquals(nullRequest1.hashCode(), nullRequest2.hashCode());
    }

    @Test
    void testBrandRequestFieldAnnotations() throws NoSuchFieldException {
        Field nameField = BrandRequest.class.getDeclaredField("name");
        Field imageUrlField = BrandRequest.class.getDeclaredField("imageUrl");

        // Verify @Schema annotations on fields
        assertNotNull(nameField.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class));
        assertNotNull(imageUrlField.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class));

        // Verify requiredMode
        assertEquals(
                io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED,
                nameField.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class).requiredMode()
        );
        assertEquals(
                io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED,
                imageUrlField.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class).requiredMode()
        );
    }
}