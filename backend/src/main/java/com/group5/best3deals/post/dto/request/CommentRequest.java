package com.group5.best3deals.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class CommentRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentRequest that = (CommentRequest) o;
        return Objects.equals(content, that.content) &&
                Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, postId);
    }
}
