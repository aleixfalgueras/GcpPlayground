
# buckets

resource "google_storage_bucket_iam_member" "DCESA_tmp_bucket_admin" {
  bucket = google_storage_bucket.dataproc_tmp_bucket.name
  role   = "roles/storage.admin"
  member = "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
}

resource "google_storage_bucket_iam_member" "DCESA_stg_bucket_admin" {
  bucket = google_storage_bucket.dataproc_stg_bucket.name
  role   = "roles/storage.admin"
  member = "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
}

# roles

resource "google_project_iam_member" "DCESA_dataflow_admin" {
  project = var.project_id
  role    = "roles/dataflow.admin"
  member  = "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
}

resource "google_project_iam_member" "DCESA_dataflow_worker" {
  project = var.project_id
  role    = "roles/dataflow.worker"
  member  = "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
}

resource "google_project_iam_member" "DCESA_storage_object_admin" {
  project = var.project_id
  role    = "roles/storage.objectAdmin"
  member  = "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
}

resource "google_project_iam_member" "DCESA_artifactregistry_reader" {
  project = var.project_id
  role    = "roles/artifactregistry.reader"
  member  = "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
}
