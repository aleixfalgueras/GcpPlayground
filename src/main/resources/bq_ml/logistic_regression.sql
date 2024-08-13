
-- DATA PREPARATION

-- we cast to number to avoid categorial feature treatment for original string columns
CREATE TABLE bq_ml.weather_train_casted as SELECT
  Date,
  Location,
  WindGustDir,
  WindDir9am,
  WindDir3pm,
  RainToday,
  RainTomorrow,
  SAFE_CAST(MinTemp AS FLOAT64) AS MinTemp,
  SAFE_CAST(MaxTemp AS FLOAT64) AS MaxTemp,
  SAFE_CAST(Rainfall AS FLOAT64) AS Rainfall,
  SAFE_CAST(Evaporation AS FLOAT64) AS Evaporation,
  SAFE_CAST(Sunshine AS FLOAT64) AS Sunshine,
  SAFE_CAST(WindGustSpeed AS FLOAT64) AS WindGustSpeed,
  SAFE_CAST(WindSpeed9am AS FLOAT64) AS WindSpeed9am,
  SAFE_CAST(WindSpeed3pm AS FLOAT64) AS WindSpeed3pm,
  SAFE_CAST(Humidity9am AS FLOAT64) AS Humidity9am,
  SAFE_CAST(Humidity3pm AS FLOAT64) AS Humidity3pm,
  SAFE_CAST(Pressure9am AS FLOAT64) AS Pressure9am,
  SAFE_CAST(Pressure3pm AS FLOAT64) AS Pressure3pm,
  SAFE_CAST(Cloud9am AS FLOAT64) AS Cloud9am,
  SAFE_CAST(Cloud3pm AS FLOAT64) AS Cloud3pm,
  SAFE_CAST(Temp9am AS FLOAT64) AS Temp9am,
  SAFE_CAST(Temp3pm AS FLOAT64) AS Temp3pm
FROM
  `bq_ml.weather_train`;

-- CREATE MODEL

-- BQ does not allow null values (or equivalent) in label columns, they must be removed
-- we also need to activate AUTO_CLASS_WEIGHTS cause the date is unbalanced (YES = 31977 vs NO = 110316 rows)
CREATE OR REPLACE MODEL
  bq_ml.weather_prediction OPTIONS(MODEL_TYPE='logistic_reg',
    INPUT_LABEL_COLS=['RainTomorrow'],
    AUTO_CLASS_WEIGHTS=TRUE,
    ENABLE_GLOBAL_EXPLAIN = TRUE) AS
SELECT
  *
FROM
  bq_ml.weather_train_casted
WHERE
  RainTomorrow != 'NA';

-- PREDICT

SELECT *
FROM ML.PREDICT(MODEL `bq_ml.weather_prediction`,
TABLE `bq_ml.some_evaluation_table` , STRUCT(0.45 AS threshold))

-- EXPLAIN

SELECT *
FROM ML.GLOBAL_EXPLAIN(MODEL `bq_ml.weather_prediction`, STRUCT(TRUE AS class_level_explain)