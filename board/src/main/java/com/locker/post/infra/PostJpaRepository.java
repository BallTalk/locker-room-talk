package com.locker.post.infra;

import com.locker.post.domain.Post;
import com.locker.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
    @Query(value = """
        SELECT p.* 
          FROM post p
          JOIN board b ON p.board_id = b.id
         WHERE b.type = 'GENERAL' 
           AND b.team_code IS NULL
           AND p.del_yn = 'N'
         ORDER BY p.view_count DESC
        LIMIT 5
    """, nativeQuery = true)
    List<Post> findGeneralBoardTop5Posts();
}
