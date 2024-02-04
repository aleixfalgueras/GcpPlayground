
# buckets

resource "google_storage_bucket_iam_binding" "DCESA_tmp_bucket_admin" {
  bucket = google_storage_bucket.tmp_bucket.name
  role   = "roles/storage.admin"

  members = [
    "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
  ]
}

resource "google_storage_bucket_iam_binding" "DCESA_stg_bucket_admin" {
  bucket = google_storage_bucket.stg_bucket.name
  role   = "roles/storage.admin"

  members = [
    "serviceAccount:${data.google_compute_default_service_account.DCESA.email}"
  ]
}