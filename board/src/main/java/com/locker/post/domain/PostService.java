package com.locker.post.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 메인페이지 인기글 조회 (자유게시판 Top 5)
     */
    @Transactional(readOnly = true)
    public List<Post> getGeneralBoardTop5Posts() {
        return postRepository.findGeneralBoardTop5Posts();
    }

    /**
     * 메인페이지 팀 게시판 피드 (무한스크롤, 10개씩)
     */
    /*@Transactional(readOnly = true)
    public List<Post> getFeed(Long boardId, Long lastPostId) {
        if (lastPostId == null) {
            // 첫 페이지
            return postRepository.findTop10ByBoardIdOrderByIdDesc(boardId);
        }
        // 이후 페이지 (cursor 방식)
        return postRepository.findTop10ByBoardIdAndIdLessThanOrderByIdDesc(boardId, lastPostId);
    }*/
}
