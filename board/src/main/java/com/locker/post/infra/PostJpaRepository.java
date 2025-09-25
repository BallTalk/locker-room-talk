package com.locker.post.infra;

import com.locker.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
    @Query(value =
                """
                SELECT p.* 
                  FROM post p
                 WHERE p.board_id = :generalId
                   AND p.status = 'ACTIVE'
                 ORDER BY p.view_count DESC, p.id DESC
                 LIMIT 5
                """, nativeQuery = true
          )
    List<Post> findGeneralTop5Posts(@Param("generalId") Long generalId);


    @Query(value =
                """
                SELECT p.* 
                  FROM post p
                 WHERE p.board_id = :generalId
                   AND p.status = 'ACTIVE'
                 ORDER BY p.id DESC
                 LIMIT 10
                """, nativeQuery = true
          )
    List<Post> findGeneralLatest10Posts(@Param("generalId") Long generalId);

    @Query(value =
                """
                SELECT p.* 
                  FROM post p
                 WHERE p.board_id = :generalId
                   AND p.status = 'ACTIVE'
                   AND p.id < :lastPostId
                 ORDER BY p.id DESC
                 LIMIT 10
                """, nativeQuery = true
          )
    List<Post> findGeneralNext10Posts(@Param("generalId") Long generalId,
                                      @Param("lastPostId") Long lastPostId);

}
