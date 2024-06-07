
CREATE TABLE testing.students_ingestion_time_daily (
    name STRING,
    surname STRING,
    age INT
)
PARTITION BY _PARTITIONDATE; -- equivalent to DATE(_PARTITIONTIME)

CREATE TABLE testing.students_ingestion_time_hourly (
    name STRING,
    surname STRING,
    age INT
)
PARTITION BY TIMESTAMP_TRUNC(_PARTITIONTIME, HOUR);
