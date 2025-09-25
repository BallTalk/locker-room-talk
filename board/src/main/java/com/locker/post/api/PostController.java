package com.locker.post.api;

import com.locker.common.pagination.PageResponse;
import com.locker.post.application.PostFacade;
import com.locker.post.application.PostInfo;
import com.locker.post.domain.PostKeywordType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Post API", description = "게시글 관련 API 입니다.")
public class PostController {

    private final PostFacade postFacade;

    // 여러 게시판을 쓴다면 general -> {boardType}
    @GetMapping("/general/feed")
    @Operation(
            summary = "자유게시판 최신글 조회(무한스크롤)",
            description = "자유게시판 최신글을 cursor(lastPostId) 기반으로 10개씩 조회합니다."
    )
    public ResponseEntity<List<PostResponse>> getGeneralFeed(
            @RequestParam(required = false)
            @Positive(message = "POST_ID_MUST_BE_POSITIVE")
            Long lastPostId
    ) {
        List<PostInfo> infos = postFacade.getGeneralFeed(lastPostId);
        return ResponseEntity.ok(infos.stream().map(PostResponse::from).toList());
    }

    @GetMapping("/{boardId}")
    @Operation(
            summary = "게시판의 게시글 목록 조회 (페이지네이션)",
            description = "boardId 구분으로 최신순으로 페이지네이션 조회합니다."
    )
    public ResponseEntity<PageResponse<PostResponse>> getBoardPosts(
            @PathVariable @Positive(message = "BOARD_ID_MUST_BE_POSITIVE") Long boardId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) PostKeywordType keywordType,
            @ParameterObject @PageableDefault(size = 10)
            Pageable pageable
    ) {
        Page<PostInfo> infos = postFacade.getBoardPosts(boardId, pageable, keyword, keywordType);
        return ResponseEntity.ok(PageResponse.of(infos.map(PostResponse::from)));
    }


}
