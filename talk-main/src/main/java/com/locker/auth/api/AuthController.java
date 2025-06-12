package com.locker.auth.api;

import com.locker.auth.application.AuthService;
import com.locker.auth.application.JwtBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtBlacklistService blacklistService;


    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "아이디/비밀번호로 로그인하고 JSON_WEB_TOKEN 을 발급받습니다."
    )
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req
    ) {
        LoginResponse resp = authService.login(req.toCommand());
        return ResponseEntity.ok(resp);
    }


    @PostMapping("/logout")
    @Operation(
            summary     = "로그아웃",
            description = "현재 요청의 JSON_WEB_TOKEN 을 Redis 블랙리스트에 등록하여 즉시 무효화합니다. " +
                          "블랙리스트에는 해당 토큰의 만료 시점까지 저장됩니다."
    )
    public ResponseEntity<Void> logout(
            @RequestHeader(value="Authorization", required=false) String header
    ) {
        authService.resolveToken(header)
                .ifPresent(blacklistService::blacklist);
        return ResponseEntity.noContent().build();
    }

}