package com.group5.best3deals.wishlist.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeProductId;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Wishlist(Long id, Long userId, Long storeProductId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.storeProductId = storeProductId;
        this.createdAt = createdAt;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wishlist wishlist = (Wishlist) o;
        return Objects.equals(userId, wishlist.userId) &&
                Objects.equals(storeProductId, wishlist.storeProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, storeProductId);
    }
}
