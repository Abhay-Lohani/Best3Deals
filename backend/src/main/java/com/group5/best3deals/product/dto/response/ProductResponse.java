package com.group5.best3deals.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private String imgUrl;
//
//    private double price;
//
//    private int quantityInStock;

    private Long categoryId;
//
//    private Long storeId;
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProductResponse that = (ProductResponse) o;
    return Objects.equals(id, that.id);
}

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
