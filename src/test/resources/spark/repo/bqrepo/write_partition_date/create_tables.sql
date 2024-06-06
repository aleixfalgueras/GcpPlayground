CREATE TABLE testing.students_hourly (
    name STRING,
    surname STRING,
    age INT,
    birth_timestamp DATETIME NOT NULL
)
PARTITION BY DATETIME_TRUNC(birth_timestamp, HOUR);


CREATE TABLE testing.students_daily (
    name STRING,
    surname STRING,
    age INT,
    birth_date DATE NOT NULL
)
PARTITION BY birth_date;

CREATE TABLE testing.students_daily_options (
    name STRING,
    surname STRING,
    age INT,
    birth_date DATE NOT NULL
)
PARTITION BY birth_date
OPTIONS (
    partition_expiration_days = 2,
    require_partition_filter = TRUE);

CREATE TABLE testing.students_monthly(
    name STRING,
    surname STRING,
    age INT,
    birth_date DATE NOT NULL
)
PARTITION BY DATE_TRUNC(birth_date, MONTH);

CREATE TABLE testing.students_yearly (
    name STRING,
    surname STRING,
    age INT,
    birth_date DATE NOT NULL
)
PARTITION BY DATE_TRUNC(birth_date, YEAR);
