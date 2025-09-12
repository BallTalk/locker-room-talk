DROP TABLE IF EXISTS `post_comment_like`;
DROP TABLE IF EXISTS `post_bookmark`;
DROP TABLE IF EXISTS `post_like`;
DROP TABLE IF EXISTS `post_comment`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `board`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
    code                VARCHAR(20)   PRIMARY KEY COMMENT '팀 코드 (불변, 시스템 식별자)',
    name_en             VARCHAR(100)  NOT NULL COMMENT '팀 영문 이름 (변경 가능)',
    name_kr             VARCHAR(100)  NOT NULL COMMENT '팀 한글 이름 (변경 가능)',
    logo_url            VARCHAR(255)  NULL COMMENT '팀 로고',
    is_active           CHAR(1)       NOT NULL DEFAULT 'Y' COMMENT '사용 여부',
    created_by          VARCHAR(50)   NOT NULL COMMENT '생성자',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by          VARCHAR(50)   NOT NULL COMMENT '변경자',
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일'
) ENGINE=InnoDB COMMENT='팀 테이블';

CREATE TABLE `user` (
    id                  BIGINT        AUTO_INCREMENT PRIMARY KEY COMMENT '유저 ID',
    login_id            VARCHAR(20)   NULL UNIQUE COMMENT '아이디',
    provider            VARCHAR(10)   NOT NULL DEFAULT 'LOCAL' COMMENT '가입 방식',
                                      CHECK (provider IN ('LOCAL','GOOGLE','KAKAO')),
    provider_id         VARCHAR(100)  NULL                COMMENT 'OAuth 공급자 ID',
    password            VARCHAR(255)  NULL                COMMENT '패스워드(소셜로그인 = null)',
    nickname            VARCHAR(20)   NOT NULL            COMMENT '닉네임',
    phone_number        VARCHAR(11)   NULL UNIQUE         COMMENT '휴대폰 번호 (하이픈(-) 제외 후 저장)',
    team_code           VARCHAR(20)   NOT NULL            COMMENT '유저 응원팀 (team.code)',
    profile_image_url   VARCHAR(255)  NULL                COMMENT '프로필 이미지 URL',
    status_message      VARCHAR(200)  NULL                COMMENT '한 줄 상태 메시지',
    status              VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
                                      CHECK (status IN ('ACTIVE', 'SUSPENDED', 'BANNED', 'WITHDRAWN', 'DORMANT')),
    last_login_at       DATETIME      NULL                COMMENT '마지막 로그인 날짜',
    login_fail_count    INT           NOT NULL DEFAULT 0  COMMENT '로그인 실패 횟수',
    deleted_at          DATETIME      NULL                COMMENT '탈퇴 시 탈퇴 날짜',
    created_by          VARCHAR(50)   NOT NULL COMMENT '생성자',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by          VARCHAR(50)   NOT NULL COMMENT '변경자',
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일',
    INDEX idx_status      (status),
    INDEX idx_last_login  (last_login_at),
    CONSTRAINT fk_user_team FOREIGN KEY (team_code) REFERENCES team(code)
) ENGINE=InnoDB COMMENT='유저 테이블';


CREATE TABLE `board` (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시판 ID',
    type                VARCHAR(20)   NOT NULL COMMENT '게시판 유형 (GENERAL, NOTICE, QNA, TEAM)',
                        CHECK (type IN ('GENERAL','NOTICE','QNA','TEAM')),
    team_code           VARCHAR(20)   NULL COMMENT '팀 게시판일 경우 team.code (GENERAL/NOTICE/QNA는 NULL 허용)',
    name                VARCHAR(100)  NOT NULL COMMENT '게시판 이름',
    description         VARCHAR(255)  NULL COMMENT '게시판 설명',
    is_active           CHAR(1)       NOT NULL DEFAULT 'Y' COMMENT '사용 여부 (Y/N)',
    allow_anonymous     CHAR(1)       NOT NULL DEFAULT 'N' COMMENT '익명 글 허용 여부',
    post_count          INT           NOT NULL DEFAULT 0 COMMENT '게시글 수 (캐싱)',
    comment_count       INT           NOT NULL DEFAULT 0 COMMENT '댓글 수 (캐싱)',
    created_by          BIGINT        NOT NULL COMMENT '생성자',
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by          BIGINT        NOT NULL COMMENT '변경자',
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일',

    INDEX idx_board_type (type),
    INDEX idx_team_code (team_code),
    INDEX idx_is_active (is_active),

    CONSTRAINT fk_board_team FOREIGN KEY (team_code) REFERENCES team(code)
) ENGINE=InnoDB COMMENT='게시판 테이블';


CREATE TABLE `post` (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 ID',
    board_id            BIGINT        NOT NULL COMMENT '게시판 ID',
    title               VARCHAR(200)  NOT NULL COMMENT '게시글 제목',
    content             MEDIUMTEXT    NOT NULL COMMENT '게시글 본문',
    author_id           BIGINT        NOT NULL COMMENT '작성자 (user.id)',
    is_anonymous        CHAR(1)       NOT NULL DEFAULT 'N' COMMENT '익명 여부 (Y/N)',
    status              VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT '게시글 상태 (ACTIVE, HIDDEN, DELETED)',
                        CHECK (status IN ('ACTIVE', 'HIDDEN', 'DELETED')),
    view_count          INT           NOT NULL DEFAULT 0 COMMENT '조회수',
    like_count          INT           NOT NULL DEFAULT 0 COMMENT '좋아요 수 (캐싱)',
    comment_count       INT           NOT NULL DEFAULT 0 COMMENT '댓글 수 (캐싱)',
    created_by          BIGINT        NOT NULL COMMENT '생성자',

    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by          BIGINT        NOT NULL COMMENT '변경자',
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일',

    INDEX idx_post_board (board_id, created_at DESC),
    INDEX idx_post_author (author_id),
    INDEX idx_post_status (status),

    CONSTRAINT fk_post_board FOREIGN KEY (board_id) REFERENCES board(id),
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES user(id)
) ENGINE=InnoDB COMMENT='게시글 테이블';


CREATE TABLE `post_comment` (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 ID',
    post_id         BIGINT          NOT NULL COMMENT '게시글 ID',
    author_id       BIGINT          NOT NULL COMMENT '작성자',
    parent_id       BIGINT          NULL COMMENT '부모 댓글 ID',
    content         VARCHAR(1000)   NOT NULL COMMENT '댓글 본문',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '댓글 상태 (ACTIVE, HIDDEN, DELETED)',
                    CHECK (status IN ('ACTIVE', 'HIDDEN', 'DELETED')),
    like_count      INT             NOT NULL DEFAULT 0 COMMENT '좋아요 수 (캐싱)',
    created_by      BIGINT          NOT NULL COMMENT '생성자',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    updated_by      BIGINT          NOT NULL COMMENT '변경자',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일',

    INDEX idx_comment_post (post_id, created_at),
    INDEX idx_comment_author (author_id),
    INDEX idx_comment_status (status),

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES user(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES post_comment(id)
) ENGINE=InnoDB COMMENT='댓글 테이블';


CREATE TABLE `post_like` (
    post_id     BIGINT      NOT NULL COMMENT '게시글 ID',
    user_id     BIGINT      NOT NULL COMMENT '유저 ID',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',

    PRIMARY KEY (post_id, user_id),
    INDEX idx_like_user (user_id),

    CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB COMMENT='게시글 좋아요 테이블';


CREATE TABLE `post_bookmark` (
    post_id     BIGINT      NOT NULL COMMENT '게시글 ID',
    user_id     BIGINT      NOT NULL COMMENT '유저 ID',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '북마크 등록 시각',

    PRIMARY KEY (post_id, user_id),
    INDEX idx_bm_user (user_id),

    CONSTRAINT fk_bm_post FOREIGN KEY (post_id) REFERENCES post(id),
    CONSTRAINT fk_bm_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB COMMENT='게시글 북마크 테이블';


CREATE TABLE `post_comment_like` (
    comment_id  BIGINT      NOT NULL COMMENT '댓글 ID',
    user_id     BIGINT      NOT NULL COMMENT '유저 ID',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 등록 시각',

    PRIMARY KEY (comment_id, user_id),
    INDEX idx_comment_like_user (user_id),

    CONSTRAINT fk_comment_like_comment FOREIGN KEY (comment_id) REFERENCES post_comment(id),
    CONSTRAINT fk_comment_like_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB COMMENT='댓글 좋아요 테이블';

