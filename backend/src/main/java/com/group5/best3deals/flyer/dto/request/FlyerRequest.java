package com.group5.best3deals.flyer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlyerRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    private String description;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageUrl;

    private Long storeId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlyerRequest that = (FlyerRequest) o;
        return Objects.equals(storeId, that.storeId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, name);
    }
}
