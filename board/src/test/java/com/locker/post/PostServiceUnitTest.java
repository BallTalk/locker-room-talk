package com.locker.post;

import com.locker.board.domain.BoardType;
import com.locker.post.domain.Post;
import com.locker.post.domain.PostRepository;
import com.locker.post.domain.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private static Post createDummy(Long id, String title, int viewCount) {
        return Post.builder()
                .id(id)
                .boardId(1L)
                .authorId(1L)
                .title(title)
                .content("dummy")
                .isAnonymous("N")
                .status("ACTIVE")
                .viewCount(viewCount)
                .likeCount(0)
                .commentCount(0)
                .build();
    }

    @Test
    void 자유게시판_조회수_탑5_게시글_조회시_게시글이_없으면_빈리스트를_반환한다() {
        // given
        given(postRepository.findGeneralTop5Posts(BoardType.GENERAL_ID)).willReturn(List.of());

        // when
        List<Post> result = postService.getGeneralTop5Posts();

        // then
        assertThat(result).isEmpty();
        verify(postRepository, times(1)).findGeneralTop5Posts(BoardType.GENERAL_ID);
    }

    @Test
    void 자유게시판_조회수_탑5_게시글_조회시_게시글이_있으면_리스트를_그대로_반환한다() {
        // given
        List<Post> mockPosts = List.of(createDummy(1L, "title", 10));
        given(postRepository.findGeneralTop5Posts(BoardType.GENERAL_ID)).willReturn(mockPosts);

        // when
        List<Post> result = postService.getGeneralTop5Posts();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("title");
        verify(postRepository, times(1)).findGeneralTop5Posts(BoardType.GENERAL_ID);
    }

    @Test
    void 자유게시판_피드_조회시_lastPostId가_null이면_Latest10Posts를_호출한다() {
        // given
        List<Post> mockPosts = List.of(createDummy(1L, "title1", 5));
        given(postRepository.findGeneralLatest10Posts(BoardType.GENERAL_ID)).willReturn(mockPosts);

        // when
        List<Post> result = postService.getGeneralFeed(null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("title1");
        verify(postRepository, times(1)).findGeneralLatest10Posts(BoardType.GENERAL_ID);
        verify(postRepository, times(0)).findGeneralNext10Posts(BoardType.GENERAL_ID, 0L);
    }

    @Test
    void 자유게시판_피드_조회시_lastPostId가_있으면_Next10Posts를_호출한다() {
        // given
        Long lastPostId = 100L;
        List<Post> mockPosts = List.of(createDummy(2L, "title2", 7));
        given(postRepository.findGeneralNext10Posts(BoardType.GENERAL_ID, lastPostId)).willReturn(mockPosts);

        // when
        List<Post> result = postService.getGeneralFeed(lastPostId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("title2");
        verify(postRepository, times(1)).findGeneralNext10Posts(BoardType.GENERAL_ID, lastPostId);
        verify(postRepository, times(0)).findGeneralLatest10Posts(BoardType.GENERAL_ID);
    }

}
