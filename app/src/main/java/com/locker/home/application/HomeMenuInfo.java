package com.locker.home.application;

import com.locker.menu.domain.Menu;

public record HomeMenuInfo(
        Long id,
        String name,
        String position,
        String path,
        Integer sortOrder
) {
    public static HomeMenuInfo from(Menu menu) {
        return new HomeMenuInfo(
                menu.getId(),
                menu.getName(),
                menu.getPosition(),
                menu.getPath(),
                menu.getSortOrder()
        );
    }

    public boolean isLeft() {
        return "LEFT".equalsIgnoreCase(position);
    }

    public boolean isRight() {
        return "RIGHT".equalsIgnoreCase(position);
    }

}