
# demosSA

resource "google_service_account" "demosSA" {
  account_id   = "demos-sa"
  display_name = "Demos GCS service account"
  create_ignore_already_exists = "true"
}

resource "google_service_account_key" "demosSAK" {
  service_account_id = google_service_account.demosSA.name
  public_key_type    = "TYPE_X509_PEM_FILE"
}