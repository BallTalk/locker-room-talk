package com.locker.post.api;

import com.locker.post.application.PostInfo;

public record PostResponse(
        Long id,
        Long authorId,
        String title,
        String content,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount
) {
    public static PostResponse from(PostInfo info) {
        return new PostResponse(
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