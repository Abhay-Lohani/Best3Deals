package com.group5.best3deals.store.dto.request;

import com.group5.best3deals.location.dto.request.LocationRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    private String imageUrl;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocationRequest location;

    private Long brandId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreRequest that = (StoreRequest) o;
        return Objects.equals(name != null ? name.toLowerCase() : null,
                that.name != null ? that.name.toLowerCase() : null) &&
                Objects.equals(brandId, that.brandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name != null ? name.toLowerCase() : null, brandId);
    }
}
