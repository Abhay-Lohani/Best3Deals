package com.group5.best3deals.flyer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FlyerProduct {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long flyerId;

    @Column(nullable = false)
    private Long storeProductId;

    @Column(nullable = false)
    private Float originalPrice;

    @Column(nullable = false)
    private Float discountedPrice;
}