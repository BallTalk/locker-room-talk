package com.locker.board;

import lombok.Getter;

@Getter
public enum BoardType {
    GENERAL("자유게시판"),
    NOTICE("공지사항"),
    QNA("Q&A"),
    TEAM("팀 게시판");

    private final String label;

    BoardType(String label) {
        this.label = label;
    }

}
