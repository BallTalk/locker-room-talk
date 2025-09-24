package com.locker.board.domain;

import lombok.Getter;

@Getter
public enum BoardType {
    GENERAL(1L, "자유게시판"),
    NOTICE(2L, "공지사항"),
    QNA(3L, "Q&A"),
    TEAM(4L, "팀 게시판");

    private final Long id;
    private final String label;

    BoardType(Long id, String label) {
        this.id = id;
        this.label = label;
    }

    public static final Long GENERAL_ID = GENERAL.id;
    public static final Long NOTICE_ID = NOTICE.id;
    public static final Long QNA_ID = QNA.id;
    public static final Long TEAM_ID = TEAM.id;
}
