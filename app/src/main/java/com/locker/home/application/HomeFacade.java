package com.locker.home.application;

import com.locker.menu.domain.MenuService;
import com.locker.team.domain.TeamService;
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
    // private final PostService postService;

    @Transactional(readOnly = true)
    public HomeInfo getHome(String loginId) {
        List<HomeMenuInfo> menuInfos = menuService.getAllMenus().stream()
                .map(HomeMenuInfo::from)
                .toList();

        HomeUserInfo userInfo = Optional.ofNullable(loginId)
                .map(userService::findByLoginId)
                .map(HomeUserInfo::from)
                .orElse(null);

        // 게시글 조회
        // List<HomePostInfo> topPosts = postService.getTopPosts();    // 상단 5개
        // List<HomePostInfo> feed = postService.getFeed();            // 무한 스크롤용 피드

        return new HomeInfo(
                menuInfos,
                /* topPosts */ null,
                /* feed */ null,
                /* user */ null
        );
    }
}
