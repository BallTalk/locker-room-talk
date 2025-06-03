package com.locker.user.api;

import com.locker.config.security.CurrentUser;
import com.locker.user.application.UserFacade;
import com.locker.user.application.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated // requestParam, pathVariable
@Tag(name = "User API", description = "유저 관련 API 입니다.")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping
    @Operation(
            summary = "회원가입",
            description = "아이디/비밀번호로 신규 회원을 등록합니다."
    )
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody SignUpRequest req
    ) {
        userFacade.signUp(req.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/exists")
    @Operation(
            summary = "아이디 중복검사",
            description = "이미 존재하는 아이디가 있는지 확인합니다."
    )
    public ResponseEntity<Boolean> exists(
            @RequestParam String loginId
    ) {
        return ResponseEntity.ok(userFacade.exists(loginId));
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 프로필 조회",
            description = "현재 로그인된 사용자의 정보를 반환합니다.(@CurrentUser)"
    )
    public ResponseEntity<UserResponse> getMyProfile(
            @CurrentUser String loginId
    ) {
        return ResponseEntity.ok(
                UserResponse.from(userFacade.getUserByLoginId(loginId))
        );
    }

}
