package com.group5.best3deals.wishlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long storeProductId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistRequest that = (WishlistRequest) o;
        return Objects.equals(storeProductId, that.storeProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeProductId);
    }
}