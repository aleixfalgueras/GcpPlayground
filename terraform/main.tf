# APIs
resource "google_project_service" "dataproc_api" {
  service = "dataproc.googleapis.com"
  project = var.project_id
}

# Buckets
resource "google_storage_bucket" "stg_bucket" {
  name = "aleix-stg-bucket"
  location = var.multiregion
}

resource "google_storage_bucket" "tmp_bucket" {
  name = "aleix-tmp-bucket"
  location = var.multiregion
}

resource "google_storage_bucket" "demos_bucket" {
  name = "aleix-demos-bucket"
  location = var.multiregion
}

# Service accounts
resource "google_service_account" "demos_gcs_sa" {
  account_id   = "demos-gcs-sa"
  display_name = "Demos GCS service account"
  create_ignore_already_exists = "true"
}

resource "google_service_account_key" "demos_gcs_sak" {
  service_account_id = google_service_account.demos_gcs_sa.name
  public_key_type    = "TYPE_X509_PEM_FILE"
}

output "demos_gcs_sak_out" {
  value = google_service_account_key.demos_gcs_sak.private_key
  sensitive = true
}

# Permissions
resource "google_project_iam_member" "demos_gcs_sa_dataproc_editor_binding" {
  project = var.project_id
  role    = "roles/dataproc.editor"
  member  = "serviceAccount:${google_service_account.demos_gcs_sa.email}"
}


resource "google_storage_bucket_iam_binding" "demos_bucket_admin" {
  bucket = google_storage_bucket.demos_bucket.name
  role   = "roles/storage.admin"

  members = [
    "serviceAccount:${google_service_account.demos_gcs_sa.email}"
  ]
}

data "google_compute_default_service_account" "default" {
}

resource "google_service_account_iam_member" "gce-default-account-iam" {
  service_account_id = data.google_compute_default_service_account.default.name
  role               = "roles/iam.serviceAccountUser"
  member             = "serviceAccount:${google_service_account.demos_gcs_sa.email}"
}