package com.locker.home.api;

import com.locker.home.application.HomePostInfo;

public record HomePostResponse(
        Long id,
        Long authorId,
        String title,
        String content,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount
) {
    public static HomePostResponse from(HomePostInfo info) {
        return new HomePostResponse(
                info.id(),
                info.authorId(),
                info.title(),
                info.content(),
                info.viewCount(),
                info.likeCount(),
                info.commentCount()
        );
    }
}