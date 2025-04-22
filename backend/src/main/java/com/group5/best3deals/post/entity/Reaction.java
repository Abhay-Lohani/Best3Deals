package com.group5.best3deals.post.entity;

import com.group5.best3deals.post.Enum.ReactionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private ReactionType reactionType;

    private Long userId;

    @Column(nullable = false)
    private Long postId;

    @Builder
    public Reaction(Long id, ReactionType reactionType, Long userId, Long postId) {
        this.id = id;
        this.reactionType = reactionType;
        this.userId = userId;
        this.postId = postId;
    }
}
