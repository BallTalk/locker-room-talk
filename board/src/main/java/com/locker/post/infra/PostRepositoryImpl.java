package com.locker.post.infra;

import com.locker.post.domain.Post;
import com.locker.post.domain.PostKeywordType;
import com.locker.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostQueryDsl postQueryDsl;
    private final PostJpaRepository postJpaRepository;

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postJpaRepository.findAll();
    }

    @Override
    public List<Post> findGeneralTop5Posts(Long generalId) {
        return postJpaRepository.findGeneralTop5Posts(generalId);
    }

    @Override
    public List<Post> findGeneralLatest10Posts(Long generalId) {
        return postJpaRepository.findGeneralLatest10Posts(generalId);
    }

    @Override
    public List<Post> findGeneralNext10Posts(Long generalId, Long lastPostId) {
        return postJpaRepository.findGeneralNext10Posts(generalId, lastPostId);
    }

    @Override
    public Page<Post> findByBoardId(Long boardId, Pageable pageable, String keyword, PostKeywordType keywordType) {
        return postQueryDsl.findByBoardId(boardId, pageable, keyword, keywordType);
    }


}
