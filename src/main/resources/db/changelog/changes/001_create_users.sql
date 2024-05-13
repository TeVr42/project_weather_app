--changelog: 001 create app_user table
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    card_number VARCHAR(50) NOT NULL
);
--rollback drop table app_user;
