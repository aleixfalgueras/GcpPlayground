
resource "google_bigquery_dataset" "BQ_dataset_spark_exercises" {
  dataset_id  = "spark_exercises"
  location    = var.multiregion
  description = "Dataset for tables used in Spark Exercises"
}

resource "google_bigquery_table" "BQ_table_products" {
  project             = var.project_id
  dataset_id          = google_bigquery_dataset.BQ_dataset_spark_exercises.dataset_id
  table_id            = "products"
  deletion_protection = false

  schema = jsonencode([
    {
      name = "product_id"
      type = "STRING"
      mode = "REQUIRED"
    },
    {
      name = "product_name"
      type = "STRING"
      mode = "NULLABLE"
    },
    {
      name = "price"
      type = "NUMERIC"
      mode = "NULLABLE"
    }
  ])
}

resource "google_bigquery_table" "BQ_table_sellers" {
  project             = var.project_id
  dataset_id          = google_bigquery_dataset.BQ_dataset_spark_exercises.dataset_id
  table_id            = "sellers"
  deletion_protection = false

  schema = jsonencode([
    {
      name = "seller_id"
      type = "STRING"
      mode = "NULLABLE"
    },
    {
      name = "seller_name"
      type = "STRING"
      mode = "NULLABLE"
    },
    {
      name = "daily_target"
      type = "INTEGER"
      mode = "NULLABLE"
    }
  ])
}

resource "google_bigquery_table" "BQ_table_sales" {
  project             = var.project_id
  dataset_id          = google_bigquery_dataset.BQ_dataset_spark_exercises.dataset_id
  table_id            = "sales"
  deletion_protection = false

  schema = jsonencode([
    {
      name = "order_id"
      type = "STRING"
      mode = "REQUIRED"
    },
    {
      name = "product_id"
      type = "STRING"
      mode = "REQUIRED"
    },
    {
      name = "seller_id"
      type = "STRING"
      mode = "REQUIRED"
    },
    {
      name = "date"
      type = "DATE"
      mode = "NULLABLE"
    },
    {
      name = "num_pieces_sold"
      type = "INT64"
      mode = "NULLABLE"
    },
    {
      name = "bill_raw_text"
      type = "STRING"
      mode = "NULLABLE"
    }
  ])
}

resource "google_bigquery_table" "BQ_table_sellers_external_table" {
  dataset_id = google_bigquery_dataset.BQ_dataset_spark_exercises.dataset_id
  table_id   = "sellers_external_table"
  deletion_protection = false

  schema = jsonencode([
    {
      name = "seller_id"
      type = "STRING"
      mode = "REQUIRED"
    },
    {
      name = "seller_name"
      type = "STRING"
      mode = "NULLABLE"
    },
    {
      name = "daily_target"
      type = "INT64"
      mode = "NULLABLE"
    }
  ])

  external_data_configuration {
    source_uris = ["gs://aleix-demos-bucket/data/external_tables/sellers_external_table.csv"]
    source_format = "CSV"

    csv_options {
      skip_leading_rows = 1
      field_delimiter   = ","
      quote             = ""
    }

    autodetect = false
  }
}

resource "google_bigquery_dataset" "BQ_dataset_dataflow_exercises" {
  dataset_id  = "dataflow_exercises"
  location    = var.multiregion
  description = "Dataset for tables used in Dataflow Exercises"
}
