package com.locker.user.domain;

import lombok.Getter;

@Getter
public enum Team {
    NOT_SET("선택 안함(소셜로그인)"),
    DOOSAN_BEARS("두산 베어스"),
    LG_TWINS("LG 트윈스"),
    KIWOM_HEROES("키움 히어로즈"),
    SAMSUNG_LIONS("삼성 라이온즈"),
    LOTTE_GIANTS("롯데 자이언츠"),
    NC_DINOS("NC 다이노스"),
    KIA_TIGERS("KIA 타이거즈"),
    SSG_LANDERS("SSG 랜더스"),
    KT_WIZ("KT 위즈"),
    HANWHA_EAGLES("한화 이글스");

    private final String label;

    Team(String label) {
        this.label = label;
    }
}