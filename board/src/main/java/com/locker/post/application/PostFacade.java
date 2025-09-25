package com.locker.post.application;

import com.locker.post.domain.Post;
import com.locker.post.domain.PostKeywordType;
import com.locker.post.domain.PostService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;

    @Transactional(readOnly = true)
    public List<PostInfo> getGeneralFeed(Long lastPostId) {
        return postService.getGeneralFeed(lastPostId)
                .stream()
                .map(PostInfo::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<PostInfo> getBoardPosts(Long boardId, Pageable pageable, String keyword, PostKeywordType keywordType) {
        Page<Post> posts = postService.findByBoardId(boardId, pageable, keyword, keywordType);
        return posts.map(PostInfo::from);
    }

}
