package com.locker.user.api;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아이디 찾기 응답 DTO")
public record FindIdResponse(
        @Schema(description = "조회된 로그인 ID", example = "user01")
        String loginId
) {
    public static FindIdResponse from(String loginId) {
        return new FindIdResponse(loginId);
    }
}