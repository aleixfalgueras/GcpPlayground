
resource "google_bigquery_dataset" "BQ_dataset_spark_exercices" {
  dataset_id  = "spark_exercices"
  location    = var.multiregion
  description = "Dataset for tables used in Spark Exercices"
}

resource "google_bigquery_table" "BQ_table_products" {
  project             = var.project_id
  dataset_id          = google_bigquery_dataset.BQ_dataset_spark_exercices.dataset_id
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
  dataset_id          = google_bigquery_dataset.BQ_dataset_spark_exercices.dataset_id
  table_id            = "sellers"
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
}

resource "google_bigquery_table" "BQ_table_sales" {
  project             = var.project_id
  dataset_id          = google_bigquery_dataset.BQ_dataset_spark_exercices.dataset_id
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
