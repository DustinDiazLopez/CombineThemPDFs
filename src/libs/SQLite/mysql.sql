DROP DATABASE IF EXISTS files;
CREATE DATABASE files;
USE files;

DROP TABLE IF EXISTS day;
CREATE TABLE day (
  day_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
  created DATE
);

DROP TABLE IF EXISTS file;
CREATE TABLE file (
  file_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,
  day_id BIGINT NOT NULL,
  file_path TEXT
);

ALTER TABLE file
ADD CONSTRAINT `File_Day_FK`
foreign key (day_id)
REFERENCES day (day_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

insert into day (created) values (CURDATE());
insert into file (day_id, file_path) values (1, "C:/hello/day/1");
insert into file (day_id, file_path) values (1, "C:/hello/day/1");
insert into file (day_id, file_path) values (1, "C:/hello/day/1");

insert into day (created) values (CURDATE());
insert into file (day_id, file_path) values (2, "C:/hello/day/2");
insert into file (day_id, file_path) values (2, "C:/hello/day/2");
insert into file (day_id, file_path) values (2, "C:/hello/day/2");

SELECT D.day_id, F.file_id, F.file_path
FROM day D
inner join file F
ON D.day_id = F.day_id
ORDER BY D.day_id ASC;