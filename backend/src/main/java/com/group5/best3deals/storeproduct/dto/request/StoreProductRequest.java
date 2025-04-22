package com.group5.best3deals.storeproduct.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@AllArgsConstructor
@Builder
@Data
public class StoreProductRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long storeId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private long productId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private long brandId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    String storeProductUrl;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private double price;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int quantityInStock;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreProductRequest that = (StoreProductRequest) o;
        return productId == that.productId &&
                brandId == that.brandId &&
                Objects.equals(storeId, that.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, productId, brandId);
    }
}
