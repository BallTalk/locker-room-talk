package com.locker.home.api;

import com.locker.auth.security.resolver.CurrentUser;
import com.locker.home.application.HomeFacade;
import com.locker.home.application.HomeInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Validated
@Tag(name = "HOME API", description = "홈 화면(메인 페이지) 관련 API 입니다.")
public class HomeController {

    private final HomeFacade homeFacade;

    @GetMapping
    @Operation(
            summary = "홈 메인 페이지 데이터 조회",
            description = "메인페이지에 필요한 데이터들을 조회해 반환합니다."
    )
    public ResponseEntity<HomeResponse> getHome() {
        HomeInfo info = homeFacade.getHome();
        return ResponseEntity.ok(HomeResponse.from(info));
    }

}
