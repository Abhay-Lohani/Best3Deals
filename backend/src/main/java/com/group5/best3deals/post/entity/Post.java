package com.group5.best3deals.post.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String imgUrl;

    @Column(nullable = false)
    private Float originalPrice;

    @Column(nullable = false)
    private Float discountedPrice;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isValid;

    // Null only when the user is deleted but the post remains
    private Long userId;

    // Null only when the store is deleted but the post remains
    private Long storeId;

    private LocalDateTime endDate;

    @CreationTimestamp
    private Timestamp createdAt;
}
