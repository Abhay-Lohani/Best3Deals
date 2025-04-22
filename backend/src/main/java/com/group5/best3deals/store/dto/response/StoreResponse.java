package com.group5.best3deals.store.dto.response;

import com.group5.best3deals.location.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class StoreResponse {
    private Long id;

    private String name;

    private String address;

    private String imgUrl;

    private Location location;

    private Long brandId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreResponse that = (StoreResponse) o;
        return Objects.equals(name != null ? name.toLowerCase() : null,
                that.name != null ? that.name.toLowerCase() : null) &&
                Objects.equals(brandId, that.brandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name != null ? name.toLowerCase() : null, brandId);
    }
}
