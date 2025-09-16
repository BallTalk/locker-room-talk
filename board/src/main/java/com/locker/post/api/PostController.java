package com.locker.post.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Validated
@Tag(name = "Post API", description = "게시글 관련 API 입니다.")
public class PostController {
}
