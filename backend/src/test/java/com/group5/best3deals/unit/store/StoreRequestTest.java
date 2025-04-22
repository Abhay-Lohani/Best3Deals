package com.group5.best3deals.unit.store;

import com.group5.best3deals.location.dto.request.LocationPutRequest;
import com.group5.best3deals.location.dto.request.LocationRequest;
import com.group5.best3deals.store.dto.request.StorePutRequest;
import com.group5.best3deals.store.dto.request.StoreRequest;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class StoreRequestTest {

    // Tests for StorePutRequest
    @Test
    void testStorePutRequest() {
        // Create test data
        LocationPutRequest location = new LocationPutRequest();
        location.setLatitude(40.7128);
        location.setLongitude(-74.0060);

        // Test constructor and getters
        StorePutRequest request = new StorePutRequest();
        request.setName("Test Store");
        request.setAddress("123 Main St");
        request.setImgUrl("store.jpg");
        request.setLocation(location);
        request.setBrandId(1L);

        assertEquals("Test Store", request.getName());
        assertEquals("123 Main St", request.getAddress());
        assertEquals("store.jpg", request.getImgUrl());
        assertEquals(location, request.getLocation());
        assertEquals(1L, request.getBrandId());

        // Test equals and hashCode
        StorePutRequest sameRequest = new StorePutRequest();
        sameRequest.setName("Test Store");

        StorePutRequest differentRequest = new StorePutRequest();
        differentRequest.setName("Different Store");

        assertEquals(request, sameRequest);
        assertEquals(request.hashCode(), sameRequest.hashCode());
        assertNotEquals(request, differentRequest);
        assertNotEquals(request.hashCode(), differentRequest.hashCode());

        // Test null and different class
        assertNotEquals(request, null);
        assertNotEquals(request, new Object());
    }

    // Tests for StoreRequest
    @Test
    void testStoreRequest() {
        // Create test data
        LocationRequest location = new LocationRequest(40.7128, -74.0060);


        // Test constructor and getters
        StoreRequest request = new StoreRequest(
                "Test Store",
                "123 Main St",
                "store.jpg",
                location,
                1L
        );

        assertEquals("Test Store", request.getName());
        assertEquals("123 Main St", request.getAddress());
        assertEquals("store.jpg", request.getImageUrl());
        assertEquals(location, request.getLocation());
        assertEquals(1L, request.getBrandId());

        // Test equals and hashCode (case insensitive name comparison)
        StoreRequest sameRequest = new StoreRequest(
                "TEST STORE",  // Different case
                "Different Address",
                "different.jpg",
                null,
                1L
        );

        StoreRequest differentRequest1 = new StoreRequest(
                "Different Store",
                "123 Main St",
                "store.jpg",
                location,
                1L
        );

        StoreRequest differentRequest2 = new StoreRequest(
                "Test Store",
                "123 Main St",
                "store.jpg",
                location,
                2L  // Different brandId
        );

        assertEquals(request, sameRequest);
        assertEquals(request.hashCode(), sameRequest.hashCode());
        assertNotEquals(request, differentRequest1);
        assertNotEquals(request.hashCode(), differentRequest1.hashCode());
        assertNotEquals(request, differentRequest2);
        assertNotEquals(request.hashCode(), differentRequest2.hashCode());

        // Test null and different class
        assertNotEquals(request, null);
        assertNotEquals(request, new Object());

        // Test with null name
        StoreRequest nullNameRequest1 = new StoreRequest(null, "Address", null, null, null);
        StoreRequest nullNameRequest2 = new StoreRequest(null, "Different", null, null, null);
        assertEquals(nullNameRequest1, nullNameRequest2);
        assertEquals(nullNameRequest1.hashCode(), nullNameRequest2.hashCode());
    }

    @Test
    void testStoreRequestNoArgsConstructor() {
        // Test no-args constructor
        StoreRequest request = new StoreRequest();
        assertNull(request.getName());
        assertNull(request.getAddress());
        assertNull(request.getImageUrl());
        assertNull(request.getLocation());
        assertNull(request.getBrandId());
    }
}