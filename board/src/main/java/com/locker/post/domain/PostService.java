package com.locker.post.domain;

import com.locker.board.domain.BoardType;
import lombok.RequiredArgsConstructor;
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
}
