package com.group5.best3deals.flyer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class FlyerProductRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long flyerId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long storeProductId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Float originalPrice;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Float discountedPrice;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlyerProductRequest that = (FlyerProductRequest) o;
        return Objects.equals(flyerId, that.flyerId) &&
                Objects.equals(storeProductId, that.storeProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flyerId, storeProductId);
    }
}

