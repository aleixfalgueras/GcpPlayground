
-- CREATE MODEL

CREATE OR REPLACE MODEL `bq_ml.bike_trips`
    TRANSFORM(start_station_name,
        end_station_name,
        subscriber_type,
        duration_minutes,
        CAST(EXTRACT(hour FROM start_time) AS string) AS ride_hour)
    OPTIONS(model_type='kmeans',
      num_trials = 6,
      num_clusters= HPARAM_RANGE(2, 7),
      kmeans_init_method='kmeans++',
      distance_type='cosine',
      min_rel_progress=0.005) AS
  SELECT
    start_time,
    start_station_name,
    end_station_name,
    subscriber_type,
    duration_minutes,
  FROM
    `bigquery-public-data.austin_bikeshare.bikeshare_trips`;
-- "kmeans++" as init method it's normally better than "random"
-- default distance type is 'euclidean', if evaluation metrics aren't good try with 'cosine'

SELECT * FROM ML.PREDICT (MODEL `bq_ml.bike_trips`, TABLE `bigquery-public-data.austin_bikeshare.bikeshare_trips`)

-- use ML.DETECT_ANOMALIES to identify outliers

SELECT * FROM ML.CENTROIDS(MODEL `bq_ml.bike_trips`)
-- the only "explain" function available