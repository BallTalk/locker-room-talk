package com.locker.home.api;

import com.locker.home.application.HomeInfo;

public record HomeResponse(
        List<MenuResponse> leftMenus,
        List<MenuResponse> rightMenus,
        List<PostResponse> topPosts,
        List<PostResponse> feed,
        UserResponse user
) {
    public static HomeResponse from(HomeInfo info) {
        return new HomeResponse(
                info.leftMenus().stream().map(MenuResponse::from).toList(),
                info.rightMenus().stream().map(MenuResponse::from).toList(),
                info.topPosts().stream().map(PostResponse::from).toList(),
                info.feed().stream().map(PostResponse::from).toList(),
                UserResponse.from(info.user())
        );
    }
}