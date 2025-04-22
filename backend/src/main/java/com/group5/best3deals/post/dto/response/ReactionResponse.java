package com.group5.best3deals.post.dto.response;

import com.group5.best3deals.post.Enum.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ReactionResponse {

    private Long id;

    private ReactionType reactionType;

    private Long userId;

    private Long postId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionResponse that = (ReactionResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(reactionType, that.reactionType) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reactionType, userId, postId);
    }

    @Override
    public String toString() {
        return "ReactionResponse{" +
                "id=" + id +
                ", reactionType=" + reactionType +
                ", userId=" + userId +
                ", postId=" + postId +
                '}';
    }
}
