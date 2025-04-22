package com.group5.best3deals.flyer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class FlyerPutRequest {
    private String name;
    private String description;
    private String imageUrl;
    private Long storeId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlyerPutRequest that = (FlyerPutRequest) o;

        // If both storeId and name are present, compare both
        if (storeId != null && name != null) {
            return Objects.equals(storeId, that.storeId) &&
                    Objects.equals(name, that.name);
        }
        // Otherwise use whichever is not null
        return storeId != null ? Objects.equals(storeId, that.storeId) :
                name != null ? Objects.equals(name, that.name) :
                        // Both null -> only equal if same instance (handled by first check)
                        false;
    }

    @Override
    public int hashCode() {
        if (storeId != null && name != null) {
            return Objects.hash(storeId, name);
        }
        return storeId != null ? Objects.hash(storeId) :
                name != null ? Objects.hash(name) :
                        0; // Both null -> consistent hash
    }
}
