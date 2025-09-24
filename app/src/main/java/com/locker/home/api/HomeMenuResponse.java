package com.locker.home.api;

import com.locker.home.application.HomeMenuInfo;

public record HomeMenuResponse(
        Long id,
        String name,
        String position,
        String path,
        Integer sortOrder
) {
    public static HomeMenuResponse from(HomeMenuInfo info) {
        return new HomeMenuResponse(
                info.id(),
                info.name(),
                info.position(),
                info.path(),
                info.sortOrder()
        );
    }
}