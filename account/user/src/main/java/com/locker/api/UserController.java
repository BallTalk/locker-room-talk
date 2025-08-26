package com.locker.api;

import com.locker.security.resolver.CurrentUser;
import com.locker.application.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<MyProfileResponse> getMyProfile(
            @CurrentUser String loginId
    ) {
        return ResponseEntity.ok(
                MyProfileResponse.from(userFacade.getUserByLoginId(loginId))
        );
    }

    @PatchMapping("/me")
    @Operation(
            summary = "내 프로필 수정",
            description = "닉네임, 프로필 이미지, 상태 메시지를 업데이트합니다. (@CurrentUser)"
    )
    public ResponseEntity<Void> updateMyProfile(
            @CurrentUser String loginId,
            @Valid @RequestBody UpdateProfileRequest req
    ) {
        userFacade.updateProfile(loginId, req.toCommand());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{loginId}")
    @Operation(
            summary = "특정 유저 프로필 조회",
            description = "다른 사용자의 공개 프로필(닉네임, 팀 등) 정보를 반환합니다."
    )
    public ResponseEntity<UserProfileResponse> getPublicProfile(
            @PathVariable String loginId
    ) {
        return ResponseEntity.ok(
                UserProfileResponse.from(userFacade.getUserByLoginId(loginId))
        );
    }

    @PatchMapping("/me/password")
    @Operation(
            summary = "비밀번호 변경",
            description = "기존 비밀번호 확인 후 새 비밀번호로 변경합니다. (@CurrentUser)"
    )
    public ResponseEntity<Void> changePassword(
            @CurrentUser String loginId,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        userFacade.changePassword(loginId, req.toCommand());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인된 사용자를 탈퇴 처리합니다. (@CurrentUser)"
    )
    public ResponseEntity<Void> withdraw(@CurrentUser String loginId) {
        userFacade.withdraw(loginId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find-id")
    @Operation(
            summary = "아이디 찾기",
            description = "전화번호와 SMS 인증 코드를 받아 검증 후 매핑된 로그인 ID를 반환합니다."
    )
    public ResponseEntity<FindIdResponse> findIdWithSms(@Valid @RequestBody FindIdRequest req) {
        String loginId = userFacade.findIdWithSms(req.toCommand());
        return ResponseEntity.ok(FindIdResponse.from(loginId));
    }

    @GetMapping("/check/{loginId}")
    @Operation(summary = "아이디 존재 확인",
            description = "주어진 로그인 아이디가 가입된 계정인지 확인합니다. 비밀번호 재설정 과정에서 필요합니다.")
    public ResponseEntity<Void> checkLoginId(
            @PathVariable
            @NotBlank(message = "LOGIN_ID_REQUIRED")
            @Size(min = 5, max = 20, message = "LOGIN_ID_LENGTH_INVALID")
            @Pattern(regexp = "^[a-z0-9]{5,20}$", message = "LOGIN_ID_PATTERN_INVALID")
            String loginId
    ) {
        userFacade.getUserByLoginId(loginId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "비밀번호 재설정",
            description = "전화번호와 SMS 인증 코드를 받아 검증 후 새 비밀번호로 재설정합니다."
    )
    public ResponseEntity<Void> resetPasswordWithSms(@Valid @RequestBody ResetPasswordRequest req) {
        userFacade.resetPasswordWithSms(req.toCommand());
        return ResponseEntity.ok().build();
    }
}
