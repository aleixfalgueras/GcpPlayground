resource "google_project_iam_member" "demos_sa_dataproc_editor_binding" {
  project = var.project_id
  role    = "roles/dataproc.editor"
  member  = "serviceAccount:${google_service_account.demos_sa.email}"
}


resource "google_storage_bucket_iam_binding" "demos_bucket_admin" {
  bucket = google_storage_bucket.demos_bucket.name
  role   = "roles/storage.admin"

  members = [
    "serviceAccount:${google_service_account.demos_sa.email}"
  ]
}

data "google_compute_default_service_account" "default" {
}

resource "google_service_account_iam_member" "gce-default-account-iam" {
  service_account_id = data.google_compute_default_service_account.default.name
  role               = "roles/iam.serviceAccountUser"
  member             = "serviceAccount:${google_service_account.demos_sa.email}"
}