package com.group5.best3deals.unit.post;

import com.group5.best3deals.post.dto.request.PostPutRequest;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class PostPutRequestTest {

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        PostPutRequest request = new PostPutRequest();
        request.setTitle("Title");
        request.setContent("Content");
        request.setImgUrl("img.jpg");
        request.setOriginalPrice(100.0f);
        request.setDiscountedPrice(80.0f);
        request.setIsValid(true);
        request.setStoreId(1L);
        request.setEndDate(now);

        String expected = String.format(
                "PostPutRequest{title='Title', content='Content', imgUrl='img.jpg', originalPrice=100.0, " +
                        "discountedPrice=80.0, isValid=true, storeId=1, endDate=%s}",
                now.toString()
        );
        assertEquals(expected, request.toString());
    }

    @Test
    void testGettersAndSetters() {
        LocalDateTime now = LocalDateTime.now();
        PostPutRequest request = new PostPutRequest();

        request.setTitle("Title");
        request.setContent("Content");
        request.setImgUrl("img.jpg");
        request.setOriginalPrice(100.0f);
        request.setDiscountedPrice(80.0f);
        request.setIsValid(true);
        request.setStoreId(1L);
        request.setEndDate(now);

        assertEquals("Title", request.getTitle());
        assertEquals("Content", request.getContent());
        assertEquals("img.jpg", request.getImgUrl());
        assertEquals(100.0f, request.getOriginalPrice());
        assertEquals(80.0f, request.getDiscountedPrice());
        assertTrue(request.getIsValid());
        assertEquals(1L, request.getStoreId());
        assertEquals(now, request.getEndDate());
    }

    @Test
    void testToStringWithNullValues() {
        PostPutRequest request = new PostPutRequest();
        String expected = "PostPutRequest{title='null', content='null', imgUrl='null', originalPrice=null, " +
                "discountedPrice=null, isValid=null, storeId=null, endDate=null}";
        assertEquals(expected, request.toString());
    }
}