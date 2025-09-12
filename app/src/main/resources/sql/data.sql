-- team 메타데이터
INSERT INTO `team` (code, name_en, name_kr, logo_url, created_by, updated_by)
VALUES
    ('TEAM000', 'NOT_SET',       '선택 안함',      NULL, 'system', 'system'),
    ('TEAM001', 'DOOSAN_BEARS',  '두산 베어스',   NULL, 'system', 'system'),
    ('TEAM002', 'LG_TWINS',      'LG 트윈스',     NULL, 'system', 'system'),
    ('TEAM003', 'KIWOM_HEROES',  '키움 히어로즈', NULL, 'system', 'system'),
    ('TEAM004', 'SAMSUNG_LIONS', '삼성 라이온즈', NULL, 'system', 'system'),
    ('TEAM005', 'LOTTE_GIANTS',  '롯데 자이언츠', NULL, 'system', 'system'),
    ('TEAM006', 'NC_DINOS',      'NC 다이노스',   NULL, 'system', 'system'),
    ('TEAM007', 'KIA_TIGERS',    'KIA 타이거즈',  NULL, 'system', 'system'),
    ('TEAM008', 'SSG_LANDERS',   'SSG 랜더스',    NULL, 'system', 'system'),
    ('TEAM009', 'KT_WIZ',        'KT 위즈',       NULL, 'system', 'system'),
    ('TEAM010', 'HANWHA_EAGLES', '한화 이글스',   NULL, 'system', 'system');

-- 유저 정보
INSERT INTO `user` (login_id, password, nickname, team_code, created_by, updated_by)
VALUES
    ('test01', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저1', 'TEAM002', 'system', 'system'), -- LG_TWINS
    ('test02', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저2', 'TEAM001', 'system', 'system'), -- DOOSAN_BEARS
    ('test03', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저3', 'TEAM006', 'system', 'system'), -- NC_DINOS
    ('test04', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저4', 'TEAM004', 'system', 'system'), -- SAMSUNG_LIONS
    ('test05', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저5', 'TEAM007', 'system', 'system'), -- KIA_TIGERS
    ('test06', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저6', 'TEAM008', 'system', 'system'), -- SSG_LANDERS
    ('test07', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저7', 'TEAM005', 'system', 'system'), -- LOTTE_GIANTS
    ('test08', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저8', 'TEAM009', 'system', 'system'), -- KT_WIZ
    ('test09', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저9', 'TEAM010','system', 'system'), -- HANWHA_EAGLES
    ('test10','$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저10','TEAM003','system', 'system'); -- KIWOM_HEROES

-- 암호화 전의 암호 : pw123

-- 공용 게시판 (3개)
INSERT INTO `board` (type, team_code, name, description, is_active, allow_anonymous, created_by, updated_by)
VALUES
    ('GENERAL', NULL, '자유게시판', '전체 회원이 자유롭게 소통하는 게시판', 'Y', 'N', 11, 11),
    ('NOTICE',  NULL, '공지사항',   '운영자가 공지사항을 게시하는 공간',       'Y', 'N', 11, 11),
    ('QNA',     NULL, 'Q&A',       '질문과 답변을 공유하는 공간',             'Y', 'Y', 11, 11);

-- 팀별 게시판 (10개)
INSERT INTO `board` (type, team_code, name, description, is_active, allow_anonymous, created_by, updated_by)
VALUES
    ('TEAM', 'TEAM001', '두산 베어스 팬 게시판',   '두산 베어스를 응원하는 팬들의 공간', 'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM002', 'LG 트윈스 팬 게시판',     'LG 트윈스를 응원하는 팬들의 공간',   'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM003', '키움 히어로즈 팬 게시판', '키움 히어로즈 팬들의 공간',         'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM004', '삼성 라이온즈 팬 게시판', '삼성 라이온즈 팬들의 공간',         'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM005', '롯데 자이언츠 팬 게시판', '롯데 자이언츠 팬들의 공간',         'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM006', 'NC 다이노스 팬 게시판',   'NC 다이노스를 응원하는 팬들의 공간', 'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM007', 'KIA 타이거즈 팬 게시판',  'KIA 타이거즈 팬들의 공간',          'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM008', 'SSG 랜더스 팬 게시판',    'SSG 랜더스 팬들의 공간',            'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM009', 'KT 위즈 팬 게시판',       'KT 위즈 팬들의 공간',               'Y', 'Y', 11, 11),
    ('TEAM', 'TEAM010', '한화 이글스 팬 게시판',   '한화 이글스를 응원하는 팬들의 공간', 'Y', 'Y', 11, 11);

-- 자유게시판 (board.id = 1)
INSERT INTO `post` (board_id, title, content, author_id, is_anonymous, status, view_count, like_count, comment_count, created_by, updated_by)
VALUES
    (1, '안녕하세요, 첫 글입니다!', '여기는 자유게시판입니다. 반갑습니다 😀', 1, 'N', 'ACTIVE', 10, 2, 1, 1, 1),
    (1, '오늘 경기 보신 분?', 'LG 트윈스 경기 보신 분들 얘기해요!', 2, 'Y', 'ACTIVE', 25, 5, 3, 2, 2);

-- 공지사항 (board.id = 2)
INSERT INTO `post` (board_id, title, content, author_id, is_anonymous, status, view_count, like_count, comment_count, created_by, updated_by)
VALUES
    (2, '서비스 점검 안내', '내일 새벽 2시~4시까지 서버 점검이 예정되어 있습니다.', 1, 'N', 'ACTIVE', 100, 0, 0, 1, 1);

-- Q&A (board.id = 3)
INSERT INTO `post` (board_id, title, content, author_id, is_anonymous, status, view_count, like_count, comment_count, created_by, updated_by)
VALUES
    (3, '비밀번호 재설정은 어떻게 하나요?', '로그인 비밀번호를 잊어버렸습니다. 재설정 방법 알려주세요.', 3, 'N', 'ACTIVE', 15, 1, 2, 3, 3);

-- 팀 게시판 (LG 트윈스, board.id = 5)
INSERT INTO `post` (board_id, title, content, author_id, is_anonymous, status, view_count, like_count, comment_count, created_by, updated_by)
VALUES
    (5, 'LG 트윈스 우승 가자!', '올해는 진짜 우승할 것 같습니다. 화이팅!', 1, 'N', 'ACTIVE', 50, 10, 5, 1, 1),
    (5, '오늘 경기 직관 후기', '직관 다녀왔는데 분위기 최고였습니다!', 2, 'N', 'ACTIVE', 35, 8, 4, 2, 2);

-- 팀 게시판 (두산 베어스, board.id = 4)
INSERT INTO `post` (board_id, title, content, author_id, is_anonymous, status, view_count, like_count, comment_count, created_by, updated_by)
VALUES
    (4, '두산 팬들 모여라', '올해도 화이팅입니다! 두산 짱!', 3, 'N', 'ACTIVE', 40, 6, 3, 3, 3);

-- 자유게시판 post.id = 1에 댓글
INSERT INTO `post_comment` (post_id, author_id, parent_id, content, status, like_count, created_by, updated_by)
VALUES
    (1, 2, NULL, '반가워요! 잘 부탁드립니다 😀', 'ACTIVE', 1, 2, 2),
    (1, 3, NULL, '어서오세요~!', 'ACTIVE', 0, 3, 3);

-- 자유게시판 post.id = 2에 댓글 + 대댓글
INSERT INTO `post_comment` (post_id, author_id, parent_id, content, status, like_count, created_by, updated_by)
VALUES
    (2, 1, NULL, '저도 경기 봤는데 재밌더라고요!', 'ACTIVE', 2, 1, 1),
    (2, 3, 1,   '맞아요 ㅋㅋ 진짜 치열했음', 'ACTIVE', 0, 3, 3); -- parent_id = 첫 댓글

-- 공지사항 (post.id = 3) → 댓글 없음

-- Q&A post.id = 4에 댓글
INSERT INTO `post_comment` (post_id, author_id, parent_id, content, status, like_count, created_by, updated_by)
VALUES
    (4, 2, NULL, '비밀번호 찾기 메뉴에서 가능해요!', 'ACTIVE', 3, 2, 2),
    (4, 1, NULL, '고객센터 문의하셔도 돼요.', 'ACTIVE', 1, 1, 1);

-- LG 트윈스 게시판 post.id = 5에 댓글
INSERT INTO `post_comment` (post_id, author_id, parent_id, content, status, like_count, created_by, updated_by)
VALUES
    (5, 3, NULL, '우승 가자 LG!!', 'ACTIVE', 5, 3, 3),
    (5, 2, NULL, '올해는 진짜 기대됩니다.', 'ACTIVE', 2, 2, 2);

-- LG 트윈스 게시판 post.id = 6에 댓글
INSERT INTO `post_comment` (post_id, author_id, parent_id, content, status, like_count, created_by, updated_by)
VALUES
    (6, 1, NULL, '직관 부럽네요 ㅠㅠ', 'ACTIVE', 0, 1, 1);

-- 두산 베어스 게시판 post.id = 7에 댓글
INSERT INTO `post_comment` (post_id, author_id, parent_id, content, status, like_count, created_by, updated_by)
VALUES
    (7, 2, NULL, '두산 파이팅!!', 'ACTIVE', 1, 2, 2);

-- 자유게시판 post.id = 1에 좋아요
INSERT INTO `post_like` (post_id, user_id)
VALUES
    (1, 2),
    (1, 3);

-- 자유게시판 post.id = 2에 좋아요
INSERT INTO `post_like` (post_id, user_id)
VALUES
    (2, 1),
    (2, 3);

-- Q&A post.id = 4에 좋아요
INSERT INTO `post_like` (post_id, user_id)
VALUES
    (4, 2);

-- LG 트윈스 게시판 post.id = 5에 좋아요
INSERT INTO `post_like` (post_id, user_id)
VALUES
    (5, 1),
    (5, 2),
    (5, 3);

-- LG 트윈스 게시판 post.id = 6에 좋아요
INSERT INTO `post_like` (post_id, user_id)
VALUES
    (6, 3);

-- 두산 베어스 게시판 post.id = 7에 좋아요
INSERT INTO `post_like` (post_id, user_id)
VALUES
    (7, 1),
    (7, 2);

-- 자유게시판 post.id = 1을 북마크
INSERT INTO `post_bookmark` (post_id, user_id)
VALUES
    (1, 1),
    (1, 3);

-- 자유게시판 post.id = 2을 북마크
INSERT INTO `post_bookmark` (post_id, user_id)
VALUES
    (2, 2);

-- Q&A post.id = 4을 북마크
INSERT INTO `post_bookmark` (post_id, user_id)
VALUES
    (4, 1),
    (4, 2);

-- LG 트윈스 게시판 post.id = 5 을 북마크
INSERT INTO `post_bookmark` (post_id, user_id)
VALUES
    (5, 3),
    (5, 1);

-- 두산 베어스 게시판 post.id = 7을 북마크
INSERT INTO `post_bookmark` (post_id, user_id)
VALUES
    (7, 2);

-- 자유게시판 post.id = 1 (comment.id = 1, 2)에 좋아요
INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (1, 3);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (2, 1);

-- 자유게시판 post.id = 2 (comment.id = 3, 4)에 좋아요
INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (3, 2);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (3, 3);

-- Q&A post.id = 4 (comment.id = 5, 6)에 좋아요
INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (5, 1);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (5, 3);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (6, 2);

-- LG 트윈스 게시판 post.id = 5 (comment.id = 7, 8)에 좋아요
INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (7, 1);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (7, 2);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (8, 3);

-- LG 트윈스 게시판 post.id = 6 (comment.id = 9)에 좋아요
INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (9, 2);

-- 두산 베어스 게시판 post.id = 7 (comment.id = 10)에 좋아요
INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (10, 1);

INSERT INTO `post_comment_like` (comment_id, user_id)
VALUES (10, 3);