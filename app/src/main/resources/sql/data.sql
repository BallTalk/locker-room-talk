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