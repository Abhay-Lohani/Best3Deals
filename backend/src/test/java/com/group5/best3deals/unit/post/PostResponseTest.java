package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.dto.response.PostResponse;
import com.group5.best3deals.post.entity.Reaction;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PostResponseTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        List<Reaction> reactions = List.of(new Reaction());

        PostResponse response1 = new PostResponse(1L, "Title", "Content", "img.jpg",
                100.0f, 80.0f, true, 1L, 1L, now, timestamp, reactions);
        PostResponse response2 = new PostResponse(1L, "Different", "Different", "different.jpg",
                200.0f, 160.0f, false, 2L, 2L, now.plusDays(1), timestamp, List.of());
        PostResponse different = new PostResponse(2L, "Title", "Content", "img.jpg",
                100.0f, 80.0f, true, 1L, 1L, now, timestamp, reactions);

        // Test equals (only compares id)
        assertEquals(response1, response2);
        assertNotEquals(response1, different);
        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());

        // Test hashCode (only uses id)
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), different.hashCode());
    }

    @Test
    void testGetters() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        List<Reaction> reactions = List.of(new Reaction());

        PostResponse response = new PostResponse(1L, "Title", "Content", "img.jpg",
                100.0f, 80.0f, true, 1L, 1L, now, timestamp, reactions);

        assertEquals(1L, response.getId());
        assertEquals("Title", response.getTitle());
        assertEquals("Content", response.getContent());
        assertEquals("img.jpg", response.getImgUrl());
        assertEquals(100.0f, response.getOriginalPrice());
        assertEquals(80.0f, response.getDiscountedPrice());
        assertTrue(response.getIsValid());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getStoreId());
        assertEquals(now, response.getEndDate());
        assertEquals(timestamp, response.getCreatedAt());
        assertEquals(reactions, response.getReactions());
    }
}