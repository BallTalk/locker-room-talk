package com.locker.post.application;

import com.locker.post.domain.Post;

public record PostInfo(
        Long id,
        Long authorId,
        String title,
        String content,
        Integer viewCount,
        Integer likeCount,
        Integer commentCount
) {
    public static PostInfo from(Post post) {
        return new PostInfo(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }
}