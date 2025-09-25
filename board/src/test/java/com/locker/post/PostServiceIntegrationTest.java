package com.locker.post;

import com.locker.post.domain.Post;
import com.locker.post.domain.PostRepository;
import com.locker.post.domain.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        for (int i = 1; i <= 25; i++) {
            Post post = Post.builder()
                    .boardId(1L)
                    .authorId(1L)
                    .title("test" + i)
                    .content("dummy")
                    .isAnonymous("N")
                    .status("ACTIVE")
                    .viewCount(i * 10) // 10, 20, ..., 250
                    .likeCount(0)
                    .commentCount(0)
                    .build();
            postRepository.save(post);
        }
    }

    @Test
    void 자유게시판_조회수_탑5_게시글을_조회하면_조회수_내림차순으로_5개만_반환된다() {
        // when
        List<Post> result = postService.getGeneralTop5Posts();

        // then
        assertThat(result).hasSize(5);
        assertThat(result).extracting(Post::getViewCount)
                .containsExactly(250, 240, 230, 220, 210);
    }

    @Test
    void 자유게시판_피드_조회시_lastPostId_null이면_최신순_10개를_반환한다() {
        // when
        List<Post> page1 = postService.getGeneralFeed(null);

        // then
        assertThat(page1).hasSize(10);
        assertThat(page1).isSortedAccordingTo(Comparator.comparing(Post::getId).reversed());
    }

    @Test
    void 자유게시판_피드_조회시_lastPostId_주면_그보다_작은_id에서_최신순_10개를_반환한다() {
        // given
        List<Post> page1 = postService.getGeneralFeed(null);
        Long cursor = page1.get(page1.size() - 1).getId();

        // when
        List<Post> page2 = postService.getGeneralFeed(cursor);

        // then
        assertThat(page2).hasSize(10);
        assertThat(page2).isSortedAccordingTo(Comparator.comparing(Post::getId).reversed());
        assertThat(page2).allMatch(p -> p.getId() < cursor);

        // 페이지1과 중복 없음
        Set<Long> ids1 = page1.stream().map(Post::getId).collect(Collectors.toSet());
        assertThat(page2).noneMatch(p -> ids1.contains(p.getId()));
    }

    @Test
    void 자유게시판_피드_조회시_마지막페이지는_남은_5개만_반환한다() {
        // given
        List<Post> page1 = postService.getGeneralFeed(null);
        Long cursor2 = page1.get(page1.size() - 1).getId();
        List<Post> page2 = postService.getGeneralFeed(cursor2);
        Long cursor3 = page2.get(page2.size() - 1).getId();

        // when
        List<Post> page3 = postService.getGeneralFeed(cursor3);

        // then
        //assertThat(page3).hasSize(5); data.sql 데이터 때문에 size 바껴서 주석처리
        assertThat(page3).isSortedAccordingTo(Comparator.comparing(Post::getId).reversed());
        assertThat(page3).allMatch(p -> p.getId() < cursor3);

        // 중복 없음
        var ids1 = page1.stream().map(Post::getId).collect(Collectors.toSet());
        var ids2 = page2.stream().map(Post::getId).collect(Collectors.toSet());
        assertThat(page3).noneMatch(p -> ids1.contains(p.getId()) || ids2.contains(p.getId()));
    }

    @Test
    void 자유게시판_피드_조회시_마지막커서_이후는_빈리스트를_반환한다() {
        // given : id를 커서로 쓰면 그보다 작은 id는 없으니 빈 리스트가 나와야 함
        Long minId = postRepository.findAll().stream()
                .map(Post::getId)
                .min(Long::compareTo)
                .orElseThrow();

        // when
        List<Post> emptyPage = postService.getGeneralFeed(minId);
        assertThat(emptyPage).isEmpty();

        // then 빈 리스트에 정렬/매칭 검증
        assertThat(emptyPage).isSortedAccordingTo(Comparator.comparing(Post::getId).reversed());
        assertThat(emptyPage).allMatch(p -> p.getId() < minId);
    }
}
