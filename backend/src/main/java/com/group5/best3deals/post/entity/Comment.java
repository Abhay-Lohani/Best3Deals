package com.group5.best3deals.post.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Comment(Long id, String content, Long userId, Long postId) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.postId = postId;
    }
}
