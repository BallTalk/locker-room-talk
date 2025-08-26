package com.locker.api;

import com.locker.application.loginService;
import com.locker.security.jwt.JwtProperties;
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
public class loginController {

    private final loginService authService;
    private final JwtProperties jwtProperties;


    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "아이디/비밀번호로 로그인하고 JSON_WEB_TOKEN 을 HttpOnly 쿠키로 발급받습니다."
    )
    public ResponseEntity<Void> login(
            @RequestBody @Valid LoginRequest req,
            HttpServletResponse response
    ) {
        String token = authService.login(req.toCommand());

        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS -> true
        cookie.setPath("/");
        cookie.setMaxAge((int)(jwtProperties.getExpirationMs() / 1000));
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }
}
