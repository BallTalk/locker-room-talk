/*
package com.locker.auth.api;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(

        @Schema(description = "발급된 JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.DUMMY_PAYLOAD.DUMMY_SIG")
        String token,

        @Schema(description = "토큰 유형", example = "Bearer")
        String tokenType,

        @Schema(description = "만료 시간(UTC epoch millis)", example = "1716086400000")
        Long expirationMs
) {}*/

// HttpOnly 쿠키 방식으로 JWT를 발급하도록 변경하여 LoginResponse 를 API 응답 바디로 사용하지 않음
