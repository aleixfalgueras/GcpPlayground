CREATE DATABASE demo_db;

USE demo_db;

CREATE TABLE players (
  name VARCHAR(50),
  aka VARCHAR(50)
);

INSERT INTO players (name, aka) VALUES ('aleix', 'geleix');
INSERT INTO players (name, aka) VALUES ('gerard', 'xala');

SELECT * from players;
