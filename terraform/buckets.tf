
// REQUIRED

resource "google_storage_bucket" "dataproc_stg_bucket" {
  name = "dataproc-stg-bucket-${var.project_suffix}"
  location = var.region

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 1 # days
    }
  }
}

resource "google_storage_bucket" "dataproc_tmp_bucket" {
  name = "dataproc-tmp-bucket-${var.project_suffix}"
  location = var.region

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 1 # days
    }
  }
}

resource "google_storage_bucket" "spark_history_bucket" {
  name = "spark-history-bucket-${var.project_suffix}"
  location = var.region

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

resource "google_storage_bucket" "bq_tmp_bucket" {
  name = "bq-tmp-bucket-${var.project_suffix}"
  location = var.region

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 1 # days
    }
  }
}

resource "google_storage_bucket" "project_id_cloudbuild"{
  name = "${var.project_id}_cloudbuild"
  location = var.region

  uniform_bucket_level_access = true
  force_destroy = true

  lifecycle_rule {
    action {
      type = "Delete"
    }

    condition {
      age = 1 # days
    }
  }
}

resource "google_storage_bucket" "cloud_functions_bucket"{
  name = "cloud-functions-bucket-${var.project_suffix}"
  location = var.region

  uniform_bucket_level_access = true
  force_destroy = true
}

resource "google_storage_bucket" "demos_bucket" {
  name = "demos-bucket-${var.project_suffix}"
  location = var.region

  uniform_bucket_level_access = true
}

resource "google_storage_bucket" "tmp_bucket" {
  name = "tmp-bucket-${var.project_suffix}"
  location = var.region

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
