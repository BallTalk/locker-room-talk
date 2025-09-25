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

    @Transactional(readOnly = true)
    public HomeInfo getHome(String loginId) {
        List<HomeMenuInfo> menus = menuService.getAllMenus().stream()
                .map(HomeMenuInfo::from)
                .toList();

        List<HomePostInfo> topPosts = postService.getGeneralTop5Posts().stream()
                .map(HomePostInfo::from)
                .toList();

        List<HomePostInfo> feed = postService.getGeneralFeed(null).stream()
                .map(HomePostInfo::from)
                .toList();

        return new HomeInfo(
                menus,
                topPosts,
                feed
        );
    }
}
