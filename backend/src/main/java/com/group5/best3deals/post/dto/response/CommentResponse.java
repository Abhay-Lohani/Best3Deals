package com.group5.best3deals.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;

    private String content;

    private Long userId;

    private Long postId;

    private LocalDateTime createdAt;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentResponse that = (CommentResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(content, that.content) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(postId, that.postId) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, userId, postId, createdAt);
    }

    @Override
    public String toString() {
        return "CommentResponse{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", postId=" + postId +
                ", createdAt=" + createdAt +
                '}';
    }
}
