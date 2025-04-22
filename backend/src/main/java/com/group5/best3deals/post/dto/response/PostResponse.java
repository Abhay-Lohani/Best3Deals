package com.group5.best3deals.post.dto.response;

import com.group5.best3deals.post.entity.Reaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {

    private Long id;

    private String title;

    private String content;

    private String imgUrl;

    private Float originalPrice;

    private Float discountedPrice;

    private Boolean isValid;

    private Long userId;

    private Long storeId;

    private LocalDateTime endDate;

    private Timestamp createdAt;

    private List<Reaction> reactions;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostResponse)) return false;
        PostResponse that = (PostResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
