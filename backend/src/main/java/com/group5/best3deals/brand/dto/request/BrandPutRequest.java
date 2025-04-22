package com.group5.best3deals.brand.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class BrandPutRequest {
    private String name;
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandPutRequest that = (BrandPutRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, imageUrl);
    }
}