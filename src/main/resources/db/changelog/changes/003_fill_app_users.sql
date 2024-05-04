--changelog: 003 fill app_user
INSERT INTO app_user (username,password,card_number) VALUES ('uziv','hesloh', '0000 1111 2222 3333');
INSERT INTO app_user (username,password,card_number) VALUES ('test','12345', '0000 1111 2222 4444');
--rollback delete from app_user where id in (1,2);