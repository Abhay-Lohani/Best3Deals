package com.group5.best3deals.post.Enum;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reaction Types")
public enum ReactionType {
    LIKE,
    THUMBS_UP,
    HAHA,
    WOW,
    SAD,
}