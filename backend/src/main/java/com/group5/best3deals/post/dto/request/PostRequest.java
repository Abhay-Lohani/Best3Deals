package com.group5.best3deals.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class PostRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    private String imgUrl;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Float originalPrice;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Float discountedPrice;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long storeId;

    private LocalDateTime endDate;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostRequest that = (PostRequest) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(originalPrice, that.originalPrice) &&
                Objects.equals(discountedPrice, that.discountedPrice) &&
                Objects.equals(storeId, that.storeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, content, originalPrice, discountedPrice, storeId);
    }
}
