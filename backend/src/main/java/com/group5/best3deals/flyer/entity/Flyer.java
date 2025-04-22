package com.group5.best3deals.flyer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Flyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flyer_id")
    private Long id;

    private String name;
    private String description;
    private String imageUrl;

    @JoinColumn(name = "store_id", nullable = true)
    private Long storeId;
}
