package com.group5.best3deals.post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostPutRequest {

    private String title;

    private String content;

    private String imgUrl;

    private Float originalPrice;

    private Float discountedPrice;

    private Boolean isValid;

    private Long storeId;

    private LocalDateTime endDate;
    @Override
    public String toString() {
        return String.format(
                "PostPutRequest{title='%s', content='%s', imgUrl='%s', originalPrice=%s, " +
                        "discountedPrice=%s, isValid=%s, storeId=%s, endDate=%s}",
                title != null ? title : "null",
                content != null ? content : "null",
                imgUrl != null ? imgUrl : "null",
                originalPrice != null ? originalPrice : "null",
                discountedPrice != null ? discountedPrice : "null",
                isValid != null ? isValid : "null",
                storeId != null ? storeId : "null",
                endDate != null ? endDate : "null"
        );
    }
}