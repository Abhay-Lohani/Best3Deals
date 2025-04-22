package com.group5.best3deals.wishlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import java.util.Objects;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WishlistResponse {
    private Long id;
    private Long storeProductId;
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistResponse that = (WishlistResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(storeProductId, that.storeProductId) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeProductId, createdAt);
    }
}