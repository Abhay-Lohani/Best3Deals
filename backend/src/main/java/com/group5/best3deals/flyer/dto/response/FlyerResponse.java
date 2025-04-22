package com.group5.best3deals.flyer.dto.response;

import com.group5.best3deals.flyer.entity.FlyerProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FlyerResponse {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private Long storeId;

    private List<FlyerProduct> products;
}
