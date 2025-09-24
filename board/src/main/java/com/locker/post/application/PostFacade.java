package com.locker.post.application;

import com.locker.post.domain.Post;
import com.locker.post.domain.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;

    public List<PostInfo> getGeneralFeed(Long lastPostId) {
        return postService.getGeneralFeed(lastPostId)
                .stream()
                .map(PostInfo::from)
                .toList();
    }

}
