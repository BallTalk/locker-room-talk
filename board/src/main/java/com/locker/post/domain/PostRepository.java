package com.locker.post.domain;

import org.springframework.beans.PropertyValues;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    List<Post> findAll();

    List<Post> findGeneralTop5Posts(Long generalId);

    List<Post> findGeneralLatest10Posts(Long generalId);

    List<Post> findGeneralNext10Posts(Long generalId, Long lastPostId);

}
