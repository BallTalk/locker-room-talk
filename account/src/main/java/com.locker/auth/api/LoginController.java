package com.locker.auth.api;

import com.locker.auth.application.LoginInfo;
import com.locker.auth.application.LoginService;
import com.locker.auth.security.jwt.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "로그인 API")
public class LoginController {

    private final LoginService loginService;
    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "아이디/비밀번호로 로그인하고 JSON_WEB_TOKEN 을 HttpOnly 쿠키로 발급받습니다. 응답으로 사용자 정보를 반환합니다."
    )
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletResponse response
    ) {
        LoginInfo result = loginService.login(req.toCommand());

        Cookie cookie = new Cookie("ACCESS_TOKEN", result.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS -> true
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwtProperties.getExpirationMs() / 1000));
        response.addCookie(cookie);

        LoginResponse loginResponse = LoginResponse.from(result.user());
        return ResponseEntity.ok(loginResponse);
    }
}
