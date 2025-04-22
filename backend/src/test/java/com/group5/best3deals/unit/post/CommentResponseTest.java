package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.dto.response.CommentResponse;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class CommentResponseTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        CommentResponse response1 = new CommentResponse(1L, "Content", 1L, 1L, now);
        CommentResponse response2 = new CommentResponse(1L, "Content", 1L, 1L, now);
        CommentResponse different = new CommentResponse(2L, "Different", 1L, 1L, now);

        // Test equals
        assertEquals(response1, response2);
        assertNotEquals(response1, different);
        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());

        // Test hashCode
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), different.hashCode());
    }

    @Test
    void testGetters() {
        LocalDateTime now = LocalDateTime.now();
        CommentResponse response = new CommentResponse(1L, "Content", 1L, 1L, now);

        assertEquals(1L, response.getId());
        assertEquals("Content", response.getContent());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getPostId());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        CommentResponse response = new CommentResponse(1L, "Content", 1L, 1L, now);

        String expected = "CommentResponse{id=1, content='Content', userId=1, postId=1, createdAt=" + now + "}";
        assertEquals(expected, response.toString());
    }
}