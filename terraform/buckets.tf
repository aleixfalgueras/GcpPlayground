
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

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 14 # days
    }
  }
}

resource "google_storage_bucket" "bq_tmp_bucket" {
  name = "bq-tmp-bucket"
  location = var.multiregion

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 14 # days
    }
  }
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

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 14 # days
    }
  }
}

resource "google_storage_bucket" "aleix_tmp_bucket" {
  name = "aleix-tmp-bucket"
  location = var.multiregion

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 7 # days
    }
  }
}
