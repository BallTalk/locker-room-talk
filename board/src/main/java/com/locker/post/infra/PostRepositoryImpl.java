package com.locker.post.infra;

import com.locker.post.domain.Post;
import com.locker.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository postJpaRepository;

    @Override
    public List<Post> findGeneralBoardTop5Posts() {
        return postJpaRepository.findGeneralBoardTop5Posts();
    }
}
