package com.locker.post.domain;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> findGeneralBoardTop5Posts();
}
