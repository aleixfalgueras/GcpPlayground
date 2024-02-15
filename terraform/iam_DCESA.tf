
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