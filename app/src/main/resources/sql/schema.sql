DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        id                 BIGINT        AUTO_INCREMENT PRIMARY KEY COMMENT '유저 PK ID',
                        login_id           VARCHAR(20)   NULL UNIQUE COMMENT '아이디',
                        provider           VARCHAR(10)   NOT NULL DEFAULT 'LOCAL' COMMENT '가입 방식',
                                                         CHECK (provider IN ('LOCAL','GOOGLE','KAKAO')),
                        provider_id        VARCHAR(100)  NULL                COMMENT 'OAuth 공급자 ID',
                        password           VARCHAR(255)  NULL                COMMENT '패스워드(소셜로그인 = null)',
                        nickname           VARCHAR(20)   NOT NULL            COMMENT '닉네임',
                        phone_number       VARCHAR(11)   NULL UNIQUE     COMMENT '휴대폰 번호 (하이픈(-) 제외 후 저장)',
                        favorite_team      VARCHAR(20)   NOT NULL            COMMENT '유저 응원팀 (Team enum)',
                                                         CHECK (favorite_team IN (
                                                                                 'NOT_SET',
                                                                                 'DOOSAN_BEARS',
                                                                                 'LG_TWINS',
                                                                                 'KIWOM_HEROES',
                                                                                 'SAMSUNG_LIONS',
                                                                                 'LOTTE_GIANTS',
                                                                                 'NC_DINOS',
                                                                                 'KIA_TIGERS',
                                                                                 'SSG_LANDERS',
                                                                                 'KT_WIZ',
                                                                                 'HANWHA_EAGLES'
                                                        )),
                        profile_image_url  VARCHAR(255)  NULL                COMMENT '프로필 이미지 URL',
                        status_message     VARCHAR(200)  NULL                COMMENT '한 줄 상태 메시지',
                        status             VARCHAR(10)   NOT NULL DEFAULT 'ACTIVE' COMMENT '상태',
                                                         CHECK (status IN (
                                                                      'ACTIVE',
                                                                      'SUSPENDED',
                                                                      'BANNED',
                                                                      'WITHDRAWN',
                                                                      'DORMANT'
                                                         )),
                        last_login_at      DATETIME      NULL                COMMENT '마지막 로그인 날짜',
                        login_fail_count   INT           NOT NULL DEFAULT 0  COMMENT '로그인 실패 횟수',
                        deleted_at         DATETIME      NULL                COMMENT '탈퇴 시 탈퇴 날짜',
                        created_by         VARCHAR(50)   NOT NULL COMMENT '생성자',
                        created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
                        updated_by         VARCHAR(50)   NOT NULL COMMENT '변경자',
                        updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '변경일',
                        INDEX idx_status      (status),
                        INDEX idx_last_login  (last_login_at)
    --, FOREIGN KEY (favorite_team_id) REFERENCES teams(id)
) ENGINE=InnoDB;
