package com.locker.home.application;

import com.locker.menu.domain.MenuService;
import com.locker.post.domain.PostService;
import com.locker.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final MenuService menuService;
    private final UserService userService;
    private final PostService postService;

    // 메인페이지를 조회할때마다 요청이 옴
    // feed : 항상 새로 조회 Cache-Control: no-store ???
    // top posts: 짧은 TTL 캐시(30–60초) + 글 생성/수정/삭제 시 즉시 무효화.
    // menus: “권한/역할 기반”으로 키를 나눠서 캐시(10–60분) + 메뉴/권한 변경 시 무효화.
    // user info: 프로필성 정보만 짧은 TTL 캐시(5–10분). 자주 변하는 카운트류는 분리해서 캐시 안 함.

    @Transactional(readOnly = true)
    public HomeInfo getHome(String loginId) {
        List<HomeMenuInfo> menus = menuService.getAllMenus().stream()
                .map(HomeMenuInfo::from)
                .toList();

        HomeUserInfo user = Optional.ofNullable(loginId)
                .map(userService::findByLoginId)
                .map(HomeUserInfo::from)
                .orElse(null);

        List<HomePostInfo> topPosts = postService.getGeneralTop5Posts().stream()
                .map(HomePostInfo::from)
                .toList();

        List<HomePostInfo> feed = postService.getGeneralFeed(null).stream()
                .map(HomePostInfo::from)
                .toList();

        return new HomeInfo(
                menus,
                topPosts,
                feed,
                user
        );
    }
}
