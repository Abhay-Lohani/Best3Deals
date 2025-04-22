package com.group5.best3deals.deviceToken.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeviceTokenRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String deviceToken;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceTokenRequest that = (DeviceTokenRequest) o;
        return deviceToken != null && deviceToken.equalsIgnoreCase(that.deviceToken);
    }

    @Override
    public int hashCode() {
        return deviceToken != null ? deviceToken.toLowerCase().hashCode() : 0;
    }
}
