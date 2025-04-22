package com.group5.best3deals.post.dto.request;

import com.group5.best3deals.post.Enum.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class ReactionRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private ReactionType reactionType;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long postId;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionRequest that = (ReactionRequest) o;
        return reactionType == that.reactionType &&
                Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reactionType, postId);
    }
}
