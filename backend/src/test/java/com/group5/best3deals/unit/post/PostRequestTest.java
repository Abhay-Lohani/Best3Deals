package com.group5.best3deals.unit.post;


import com.group5.best3deals.post.dto.request.PostRequest;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PostRequestTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        PostRequest request1 = new PostRequest("Title", "Content", "img.jpg", 100.0f, 80.0f, 1L, now);
        PostRequest request2 = new PostRequest("Title", "Content", "img.jpg", 100.0f, 80.0f, 1L, now);
        PostRequest different = new PostRequest("Different", "Content", "img.jpg", 100.0f, 80.0f, 1L, now);

        // Test equals
        assertEquals(request1, request2);
        assertNotEquals(request1, different);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        // Test hashCode
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), different.hashCode());
    }

    @Test
    void testGetters() {
        LocalDateTime now = LocalDateTime.now();
        PostRequest request = new PostRequest("Title", "Content", "img.jpg", 100.0f, 80.0f, 1L, now);

        assertEquals("Title", request.getTitle());
        assertEquals("Content", request.getContent());
        assertEquals("img.jpg", request.getImgUrl());
        assertEquals(100.0f, request.getOriginalPrice());
        assertEquals(80.0f, request.getDiscountedPrice());
        assertEquals(1L, request.getStoreId());
        assertEquals(now, request.getEndDate());
    }
}