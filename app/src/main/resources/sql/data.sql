INSERT INTO `user` (
    login_id,
    password,
    nickname,
    favorite_team,
    created_by,
    updated_by
) VALUES
      ('test01', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저1', 'LG_TWINS',     'system', 'system'),
      ('test02', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저2', 'DOOSAN_BEARS', 'system', 'system'),
      ('test03', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저3', 'NC_DINOS',      'system', 'system'),
      ('test04', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저4', 'SAMSUNG_LIONS', 'system', 'system'),
      ('test05', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저5', 'KIA_TIGERS',    'system', 'system'),
      ('test06', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저6', 'SSG_LANDERS',   'system', 'system'),
      ('test07', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저7', 'LOTTE_GIANTS',  'system', 'system'),
      ('test08', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저8', 'KT_WIZ',        'system', 'system'),
      ('test09', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저9', 'HANWHA_EAGLES', 'system', 'system'),
      ('test10', '$2b$12$MxVXJDKFDyMCRQf78I2jdO/rbP9fjkDU8r7cQunDDSr4YwOsFLylm', '테스트유저10','KIWOM_HEROES', 'system', 'system');

-- 암호화 전의 암호 : pw123