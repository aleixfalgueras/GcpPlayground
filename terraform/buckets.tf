resource "google_storage_bucket" "dataproc_stg_bucket" {
  name = "dataproc-stg-bucket"
  location = var.multiregion

  uniform_bucket_level_access = true
  force_destroy = true
}

resource "google_storage_bucket" "dataproc_tmp_bucket" {
  name = "dataproc-tmp-bucket"
  location = var.multiregion

  uniform_bucket_level_access = true
  force_destroy = true
}

resource "google_storage_bucket" "aleix_demos_bucket" {
  name = "aleix-demos-bucket"
  location = var.multiregion

  uniform_bucket_level_access = true
  force_destroy = true
}

resource "google_storage_bucket" "spark_history_bucket" {
  name = "spark-history-bucket"
  location = var.multiregion
  retention_policy {
    retention_period = 1209600 # 2 weeks
  }

  uniform_bucket_level_access = true
  force_destroy = true
}