resource "google_service_account" "demos_sa" {
  account_id   = "demos-sa"
  display_name = "Demos GCS service account"
  create_ignore_already_exists = "true"
}

resource "google_service_account_key" "demos_sak" {
  service_account_id = google_service_account.demos_sa.name
  public_key_type    = "TYPE_X509_PEM_FILE"
}