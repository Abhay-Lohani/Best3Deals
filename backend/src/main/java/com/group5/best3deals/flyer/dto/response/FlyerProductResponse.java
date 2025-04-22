package com.group5.best3deals.flyer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlyerProductResponse {

    private Long id;

    private Long flyerId;

    private Long storeProductId;

    private Float originalPrice;

    private Float discountedPrice;
}
