package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.dto.request.CommentRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommentRequestTest {

    @Test
    void testEqualsAndHashCode() {
        CommentRequest request1 = new CommentRequest("Content", 1L);
        CommentRequest request2 = new CommentRequest("Content", 1L);
        CommentRequest different = new CommentRequest("Different", 1L);

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
        CommentRequest request = new CommentRequest("Content", 1L);

        assertEquals("Content", request.getContent());
        assertEquals(1L, request.getPostId());
    }
}