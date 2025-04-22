package com.group5.best3deals.location.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationRequest {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Double latitude;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Double longitude;
}