package com.group5.best3deals.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private Date timestamp;
}
