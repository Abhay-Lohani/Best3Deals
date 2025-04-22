package com.group5.best3deals.unit.flyer;

import com.group5.best3deals.flyer.dto.request.FlyerProductPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerProductRequest;
import com.group5.best3deals.flyer.dto.request.FlyerPutRequest;
import com.group5.best3deals.flyer.dto.request.FlyerRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlyerRequestClassesTest {

    // ===== FlyerProductRequest Tests =====
    @Test
    void flyerProductRequest_equals_sameFields() {
        FlyerProductRequest req1 = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        FlyerProductRequest req2 = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerProductRequest_equals_differentPrices() {
        FlyerProductRequest req1 = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        FlyerProductRequest req2 = new FlyerProductRequest(1L, 100L, 15.0f, 12.0f);
        assertEquals(req1, req2); // Prices ignored
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerProductRequest_notEquals_differentFlyerId() {
        FlyerProductRequest req1 = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        FlyerProductRequest req2 = new FlyerProductRequest(2L, 100L, 10.0f, 8.0f);
        assertNotEquals(req1, req2);
    }

    @Test
    void flyerProductRequest_notEquals_differentStoreProductId() {
        FlyerProductRequest req1 = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        FlyerProductRequest req2 = new FlyerProductRequest(1L, 101L, 10.0f, 8.0f);
        assertNotEquals(req1, req2);
    }

    @Test
    void flyerProductRequest_notEquals_nullComparison() {
        FlyerProductRequest req = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        assertNotEquals(req, null);
    }

    @Test
    void flyerProductRequest_reflexivity() {
        FlyerProductRequest req = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        assertEquals(req, req);
    }

    @Test
    void flyerProductRequest_symmetry() {
        FlyerProductRequest req1 = new FlyerProductRequest(1L, 100L, 10.0f, 8.0f);
        FlyerProductRequest req2 = new FlyerProductRequest(1L, 100L, 15.0f, 12.0f);
        assertEquals(req1.equals(req2), req2.equals(req1));
    }

    // ===== FlyerPutRequest Tests =====
    @Test
    void flyerPutRequest_equals_bothFields() {
        FlyerPutRequest req1 = new FlyerPutRequest();
        req1.setStoreId(1L);
        req1.setName("Summer Sale");

        FlyerPutRequest req2 = new FlyerPutRequest();
        req2.setStoreId(1L);
        req2.setName("Summer Sale");

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerPutRequest_equals_onlyStoreId() {
        FlyerPutRequest req1 = new FlyerPutRequest();
        req1.setStoreId(1L);

        FlyerPutRequest req2 = new FlyerPutRequest();
        req2.setStoreId(1L);

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerPutRequest_equals_onlyName() {
        FlyerPutRequest req1 = new FlyerPutRequest();
        req1.setName("Summer Sale");

        FlyerPutRequest req2 = new FlyerPutRequest();
        req2.setName("Summer Sale");

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerPutRequest_notEquals_differentStoreId() {
        FlyerPutRequest req1 = new FlyerPutRequest();
        req1.setStoreId(1L);
        req1.setName("Summer Sale");

        FlyerPutRequest req2 = new FlyerPutRequest();
        req2.setStoreId(2L);
        req2.setName("Summer Sale");

        assertNotEquals(req1, req2);
    }

    @Test
    void flyerPutRequest_notEquals_differentName() {
        FlyerPutRequest req1 = new FlyerPutRequest();
        req1.setStoreId(1L);
        req1.setName("Summer Sale");

        FlyerPutRequest req2 = new FlyerPutRequest();
        req2.setStoreId(1L);
        req2.setName("Winter Sale");

        assertNotEquals(req1, req2);
    }

    @Test
    void flyerPutRequest_notEquals_nullComparison() {
        FlyerPutRequest req = new FlyerPutRequest();
        req.setStoreId(1L);
        req.setName("Summer Sale");
        assertNotEquals(req, null);
    }

    @Test
    void flyerPutRequest_hashCode_nullFields() {
        FlyerPutRequest req = new FlyerPutRequest();
        assertEquals(0, req.hashCode()); // Both fields null -> hash=0
    }

    // ===== FlyerRequest Tests =====
    @Test
    void flyerRequest_equals_sameFields() {
        FlyerRequest req1 = new FlyerRequest("Summer Sale", null, "img.jpg", 1L);
        FlyerRequest req2 = new FlyerRequest("Summer Sale", "desc", "img2.jpg", 1L);
        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerRequest_notEquals_differentStoreId() {
        FlyerRequest req1 = new FlyerRequest("Summer Sale", null, "img.jpg", 1L);
        FlyerRequest req2 = new FlyerRequest("Summer Sale", null, "img.jpg", 2L);
        assertNotEquals(req1, req2);
    }

    @Test
    void flyerRequest_notEquals_differentName() {
        FlyerRequest req1 = new FlyerRequest("Summer Sale", null, "img.jpg", 1L);
        FlyerRequest req2 = new FlyerRequest("Winter Sale", null, "img.jpg", 1L);
        assertNotEquals(req1, req2);
    }

    @Test
    void flyerRequest_hashCode_consistency() {
        FlyerRequest req = new FlyerRequest("Summer Sale", null, "img.jpg", 1L);
        int initialHash = req.hashCode();
        assertEquals(initialHash, req.hashCode()); // Multiple calls return same hash
    }

    // ===== FlyerProductPutRequest Tests =====
    @Test
    void flyerProductPutRequest_equals_sameIds() {
        FlyerProductPutRequest req1 = new FlyerProductPutRequest();
        req1.setFlyerId(1L);
        req1.setStoreProductId(100L);

        FlyerProductPutRequest req2 = new FlyerProductPutRequest();
        req2.setFlyerId(1L);
        req2.setStoreProductId(100L);

        assertEquals(req1, req2);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void flyerProductPutRequest_notEquals_differentFlyerId() {
        FlyerProductPutRequest req1 = new FlyerProductPutRequest();
        req1.setFlyerId(1L);
        req1.setStoreProductId(100L);

        FlyerProductPutRequest req2 = new FlyerProductPutRequest();
        req2.setFlyerId(2L);
        req2.setStoreProductId(100L);

        assertNotEquals(req1, req2);
    }

    @Test
    void flyerProductPutRequest_notEquals_nullComparison() {
        FlyerProductPutRequest req = new FlyerProductPutRequest();
        req.setFlyerId(1L);
        req.setStoreProductId(100L);
        assertNotEquals(req, null);
    }

    @Test
    void flyerProductPutRequest_hashCode_nullFields() {
        FlyerProductPutRequest req = new FlyerProductPutRequest();
        assertEquals(0, req.hashCode()); // Both IDs null -> hash=0
    }
}