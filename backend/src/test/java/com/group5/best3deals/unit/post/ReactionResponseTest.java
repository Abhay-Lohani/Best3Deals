package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.Enum.ReactionType;
import com.group5.best3deals.post.dto.response.ReactionResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReactionResponseTest {

    @Test
    void testEqualsAndHashCode() {
        ReactionResponse response1 = new ReactionResponse(1L, ReactionType.LIKE, 1L, 1L);
        ReactionResponse response2 = new ReactionResponse(1L, ReactionType.LIKE, 1L, 1L);
        ReactionResponse different = new ReactionResponse(2L, ReactionType.SAD, 2L, 2L);

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
        ReactionResponse response = new ReactionResponse(1L, ReactionType.LIKE, 1L, 1L);

        assertEquals(1L, response.getId());
        assertEquals(ReactionType.LIKE, response.getReactionType());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getPostId());
    }

    @Test
    void testToString() {
        ReactionResponse response = new ReactionResponse(1L, ReactionType.LIKE, 1L, 1L);

        String expected = "ReactionResponse{id=1, reactionType=LIKE, userId=1, postId=1}";
        assertEquals(expected, response.toString());
    }
}