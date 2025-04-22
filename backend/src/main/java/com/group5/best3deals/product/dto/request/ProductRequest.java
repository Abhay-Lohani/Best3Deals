package com.group5.best3deals.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ProductRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    private String imgUrl;

    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductRequest that = (ProductRequest) o;
        return Objects.equals(name, that.name); // Only compares 'name'
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); // Only uses 'name' for hash computation
    }
}


