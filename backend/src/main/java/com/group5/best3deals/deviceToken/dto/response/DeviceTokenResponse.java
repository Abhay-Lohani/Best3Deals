package com.group5.best3deals.deviceToken.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class DeviceTokenResponse {

    private Long id;

    private String deviceToken;

    private Long userId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceTokenResponse that = (DeviceTokenResponse) o;
        return Objects.equals(deviceToken, that.deviceToken) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceToken, userId);
    }
}
