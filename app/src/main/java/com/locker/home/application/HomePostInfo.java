package com.locker.home.application;

import com.locker.post.domain.Post;

public record HomePostInfo(
        Long id,
        Long authorId,
        String title,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount
) {
    public static HomePostInfo from(Post post) {
        return new HomePostInfo(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }
}