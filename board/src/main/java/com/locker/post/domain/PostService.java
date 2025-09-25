package com.locker.post.domain;

import com.locker.board.domain.BoardType;
import com.locker.post.application.PostInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getGeneralTop5Posts() {
        return postRepository.findGeneralTop5Posts(BoardType.GENERAL_ID);
    }

    public List<Post> getGeneralFeed(Long lastPostId) {
        if (lastPostId == null) {
           return postRepository.findGeneralLatest10Posts(BoardType.GENERAL_ID);
        }
        return postRepository.findGeneralNext10Posts(BoardType.GENERAL_ID, lastPostId);
    }

    public Page<Post> findByBoardId(Long boardId, Pageable pageable, String keyword, PostKeywordType keywordType) {
        return postRepository.findByBoardId(boardId, pageable, keyword, keywordType);
    }
}
