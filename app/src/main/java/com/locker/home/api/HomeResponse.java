package com.locker.home.api;

import com.locker.home.application.HomeInfo;

import java.util.List;

public record HomeResponse(
        List<HomeMenuResponse> leftMenus,
        List<HomeMenuResponse> rightMenus,
        List<HomePostResponse> topPosts,
        List<HomePostResponse> feed
) {
    public static HomeResponse from(HomeInfo info) {
        return new HomeResponse(
                info.leftMenus().stream().map(HomeMenuResponse::from).toList(),
                info.rightMenus().stream().map(HomeMenuResponse::from).toList(),
                info.topPosts().stream().map(HomePostResponse::from).toList(),
                info.feed().stream().map(HomePostResponse::from).toList()
        );
    }
}