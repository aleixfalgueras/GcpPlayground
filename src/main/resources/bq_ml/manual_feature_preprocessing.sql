
-- MANUAL FEATURE PRE-PROCESSING

-- 2 types:
-- Analytic functions: result based on statistics collected across all rows
-- Scaler functions: operate on a single row

-- ML.BUCKETIZE splits a numerical expression into buckets
-- useful for converting numerical feature into a categorical feature
SELECT
  feature_col,
  ML.BUCKETIZE(feature_col, [7, 10, 13]) AS bucket -- (∞, 7): bin_1, [7, 10): bin_2, [10, 13): bin_3, [13, ∞): bin_4
FROM
  UNNEST([2.5,12,4,5,10,15,1.3,7,9]) AS feature_col;
--  use analytical function "ML.QUANTILE_BUCKETIZE(<col_name>, <n_buckets>) over() as bucket" to automatically create the buckets

-- ML.FEATURE_CROS builds a cross feature
SELECT ML.FEATURE_CROSS(STRUCT("John" AS first_name, "Ben" AS last_name, "USA" AS country),2) AS cross_feature
-- returns a STRUCT<STRING> value that identifies all combinations of the crossed categorical features

-- ML.NGRAMS returns an ARRAY<STRING> value that contain the n-grams
SELECT ML.NGRAMS(['John', 'Ben', 'USA'], [2], '#') AS n_grams

-- ML.MIN_MAX_SCALER scales a numerical_expression to the range [0, 1]. (Normalization aka Min-Max Scaling)
SELECT
  feature_col, ML.MIN_MAX_SCALER(feature_col) OVER() AS output
FROM
  UNNEST([1,2,3,4,5]) AS feature_col;

-- ML.STANDARD_SCALER scales a numerical expression by using z-score. (Standarization aka Z-Score Normalization)
SELECT
  feature_col, ML.STANDARD_SCALER(feature_col) OVER() AS output
FROM
  UNNEST([1,2,3,4,5]) AS feature_col;
-- In contrast to normalization, standarization has no boundaries and is less sensitive to outliers.
