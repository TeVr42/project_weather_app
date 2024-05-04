--changelog: 004 fill fav_location
INSERT INTO fav_location (username,location) VALUES ('uziv', 'Ceska Lipa');
INSERT INTO fav_location (username,location) VALUES ('uziv', 'Reykjavik');
INSERT INTO fav_location (username,location) VALUES ('uziv', 'Rome');
--rollback delete from fav_location where id in (1,3);