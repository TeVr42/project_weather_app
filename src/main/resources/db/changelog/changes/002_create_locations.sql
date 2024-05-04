--changelog: 002 create fav_locations table
CREATE TABLE IF NOT EXISTS fav_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    location VARCHAR(50) NOT NULL
);
--rollback drop table fav_locations;