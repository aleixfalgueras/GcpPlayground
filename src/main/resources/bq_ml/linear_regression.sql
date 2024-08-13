
-- MODEL CREATION

CREATE OR REPLACE MODEL
  `bq_ml.house_prices` OPTIONS(model_type='linear_reg', input_label_cols=['price']) AS
SELECT
  avg_income,avg_house_age, avg_rooms, avg_bedrooms,address, population, price/100000 as price
FROM
  `bq_ml.usa_housing_train`;

-- R=-2.5!! big differences between training and evaluation loss -> overfitting symptom
-- we use L2 Regularization to solve overftting over L1 because we already have few features

CREATE OR REPLACE MODEL
  `bq_ml.house_prices_L2` OPTIONS(model_type='linear_reg', input_label_cols=['price'], 
  l2_reg = 1, early_stop = false, max_iterations = 10) AS
SELECT
  avg_income,avg_house_age, avg_rooms, avg_bedrooms,address, population, price/100000 as price
FROM
  `bq_ml.usa_housing_train`;

-- R=0.905

CREATE OR REPLACE MODEL
  `bq_ml.house_prices_no_address` OPTIONS(model_type='linear_reg', input_label_cols=['price']) AS
SELECT
  avg_income,avg_house_age, avg_rooms, avg_bedrooms, population, price/100000 as price
FROM
  `bq_ml.usa_housing_train`;

-- R=0.909 (address unique id was misleading the model)

-- TODO: Create new model with transform feature ADDRESS as a usful feature for the model (use address state letters)

-- MODEL INFO

SELECT * FROM ML.EVALUATE(MODEL `bq_ml.house_prices_no_address`) -- print evaluation metrics
SELECT * FROM ML.TRAINING_INFO(MODEL `bq_ml.house_prices_no_address`)

-- MODEL PREDICTIONS

SELECT * FROM ML.PREDICT(MODEL `bq_ml.house_prices_no_address`, TABLE `bq_ml.usa_housing_predict`)

SELECT * FROM ML.PREDICT(MODEL `bq_ml.house_prices_no_address`,
    (
    SELECT
      avg_house_age,
      avg_rooms,
      avg_bedrooms,
      avg_income,
      population,
      price/100000 AS actual_price
    FROM
      `bq_ml.usa_housing_predict_with_original_price`))

-- compare predictions

SELECT
  price AS actual_price,
  predicted_price_1,
  predicted_price AS predicted_price_2
FROM
  ML.PREDICT(MODEL `bq_ml.house_prices_no_address_with_L2`,
    (
    SELECT
      * EXCEPT (predicted_price),
      predicted_price AS predicted_price_1
    FROM
      ML.PREDICT (MODEL `bq_ml.house_prices_no_address`,
        TABLE `bq_ml.usa_housing_predict_with_original_price`)) )

-- HYPERPARAMTERS TUNING

CREATE OR REPLACE MODEL
  `bq_ml.house_prices_HT` OPTIONS(model_type='linear_reg', input_label_cols=['price'],
  early_stop = false, max_iterations = 10, num_trials = 10, l2_reg = HPARAM_RANGE(0.9, 2)) AS
SELECT
  avg_income,avg_house_age, avg_rooms, avg_bedrooms,address, population, price/100000 as price
FROM
  `bq_ml.usa_housing_train`;

SELECT * FROM ML.TRIAL_INFO(MODEL `bq_ml.house_prices_HT`) -- instead of TRAINING_INFO

-- MODEL EXPLAIN

SELECT * FROM
  ML.EXPLAIN_PREDICT(MODEL `bq_ml.house_prices_no_address`,
    TABLE `bq_ml.usa_housing_predict`,
    STRUCT(4 AS top_k_features))

SELECT * FROM ML.GLOBAL_EXPLAIN(MODEL `bq_ml.house_prices_no_address`)
-- enable_global_explain option must be true at model creation

-- ML.FEATURE_INFO function allows you to see information about the input features used to train a model.
SELECT * FROM ML.FEATURE_INFO (MODEL `bq_ml.house_prices_no_address`)
