package com.group5.best3deals.store.dto.request;
import com.group5.best3deals.location.dto.request.LocationPutRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class StorePutRequest {
    private String name;
    private String address;
    private String imgUrl;
    private LocationPutRequest location;
    private Long brandId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorePutRequest that = (StorePutRequest) o;
        return Objects.equals(name, that.name);  // Only compares 'name'
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);  // Only uses 'name' for hash computation
    }
}