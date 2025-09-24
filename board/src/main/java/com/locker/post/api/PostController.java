package com.locker.post.api;

import com.locker.post.application.PostFacade;
import com.locker.post.application.PostInfo;
import com.locker.post.domain.Post;
import com.locker.post.domain.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Validated
@Tag(name = "Post API", description = "게시글 관련 API 입니다.")
public class PostController {

    private final PostFacade postFacade;

    // 여러 게시판을 쓴다면 general -> {boardType}
    @GetMapping("/general/feed")
    @Operation(
            summary = "자유게시판 최신글 조회",
            description = "자유게시판 최신글을 10개씩 끊어서 조회합니다. " +
                    "cursor(lastPostId) 기반으로 무한스크롤로 페이징합니다."
    )
    public ResponseEntity<List<PostResponse>> getGeneralFeed(
            @RequestParam(required = false) Long lastPostId
    ) {
        List<PostInfo> infos = postFacade.getGeneralFeed(lastPostId);
        return ResponseEntity.ok(infos.stream().map(PostResponse::from).toList());
    }

}
