package com.group5.best3deals.flyer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class FlyerProductPutRequest {

    private Long flyerId;

    private Long storeProductId;

    private Float originalPrice;

    private Float discountedPrice;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlyerProductPutRequest that = (FlyerProductPutRequest) o;
        return Objects.equals(flyerId, that.flyerId) &&
                Objects.equals(storeProductId, that.storeProductId);
    }

    @Override
    public int hashCode() {
        if (flyerId == null && storeProductId == null) {
            return 0; // Explicitly return 0 if both fields are null
        }
        return Objects.hash(flyerId, storeProductId);
    }
}
