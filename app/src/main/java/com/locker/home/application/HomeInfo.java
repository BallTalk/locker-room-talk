package com.locker.home.application;

import java.util.List;

public record HomeInfo(
        List<HomeMenuInfo> leftMenus,
        List<HomeMenuInfo> rightMenus,
        List<HomePostInfo> topPosts,
        List<HomePostInfo> feed
) {
    public HomeInfo(
            List<HomeMenuInfo> allMenus,
            List<HomePostInfo> topPosts,
            List<HomePostInfo> feed
    ) {
        this(
            allMenus.stream().filter(HomeMenuInfo::isLeft).toList(),
            allMenus.stream().filter(HomeMenuInfo::isRight).toList(),
            topPosts,
            feed
        );
    }
}