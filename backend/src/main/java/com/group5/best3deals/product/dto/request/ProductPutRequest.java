package com.group5.best3deals.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class ProductPutRequest {
    private String name;
    private String description;
    private String imgUrl;
    private Double price;
    private Integer quantityInStock;
    private Long categoryId;
    private Long storeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductPutRequest that = (ProductPutRequest) o;
        return Objects.equals(name, that.name); // Only compares 'name'
    }

    @Override
    public int hashCode() {
        return Objects.hash(name); // Only uses 'name' for hash computation
    }
}