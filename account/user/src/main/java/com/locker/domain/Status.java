package com.locker.domain;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("활성"),
    SUSPENDED("일시 정지"),
    BANNED("영구 밴"),
    WITHDRAWN("탈퇴"),
    DORMANT("휴면");

    private final String label;

    Status(String label) {
        this.label = label;
    }

}
