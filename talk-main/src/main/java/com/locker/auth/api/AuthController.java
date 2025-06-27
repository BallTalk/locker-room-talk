package com.locker.auth.api;

import com.locker.auth.application.AuthService;
import com.locker.auth.application.JwtBlacklistService;
import com.locker.config.jwt.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final JwtBlacklistService blacklistService;
    private final JwtProperties jwtProperties;


    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "아이디/비밀번호로 로그인하고 JSON_WEB_TOKEN 을 HttpOnly 쿠키로 발급받습니다."
    )

    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletResponse response
    ) {
        LoginResponse loginResp = authService.login(req.toCommand());

        Cookie cookie = new Cookie("ACCESS_TOKEN", loginResp.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);  // HTTPS -> true
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwtProperties.getExpirationMs() / 1000));
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/logout")
    @Operation(
            summary     = "로그아웃",
            description = "현재 요청의 JSON_WEB_TOKEN 을 Redis 블랙리스트에 등록하여 즉시 무효화합니다. " +
                          "블랙리스트에는 해당 토큰의 만료 시점까지 저장됩니다."
    )
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Optional<String> tokenOpt = Optional.ofNullable(request.getCookies()).stream().flatMap(Arrays::stream)
                .filter(c -> "ACCESS_TOKEN".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();

        tokenOpt.ifPresent(blacklistService::blacklist);

        Cookie deleteCookie = new Cookie("ACCESS_TOKEN", null);
        deleteCookie.setPath("/");
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);   // 운영 환경이면 true
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);

        return ResponseEntity.noContent().build();
    }

}