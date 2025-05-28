package com.locker.auth.temp;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(

        @Schema(description = "발급된 JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.DUMMY_PAYLOAD.DUMMY_SIG")
        String token,

        @Schema(description = "토큰 유형", example = "Bearer")
        String tokenType,

        @Schema(description = "만료 시간(UTC epoch millis)", example = "1716086400000")
        Long expirationMs
) {}