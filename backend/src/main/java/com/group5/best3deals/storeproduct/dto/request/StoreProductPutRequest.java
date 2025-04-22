package com.group5.best3deals.storeproduct.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
public class StoreProductPutRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long storeProductId;
    private Long storeId;
    private Long productId;
    private Long brandId;
    private String storeProductUrl;
    private Double price;
    private Integer quantityInStock;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreProductPutRequest that = (StoreProductPutRequest) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(brandId, that.brandId) &&
                Objects.equals(storeId, that.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, brandId, storeId);
    }
}