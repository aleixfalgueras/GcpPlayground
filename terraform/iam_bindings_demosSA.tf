
# roles

resource "google_project_iam_member" "demosSA_dataproc_editor_binding" {
  project = var.project_id
  role    = "roles/dataproc.editor"
  member  = "serviceAccount:${google_service_account.demosSA.email}"
}

# buckets

resource "google_storage_bucket_iam_binding" "demosSA_demos_bucket_admin" {
  bucket = google_storage_bucket.demos_bucket.name
  role   = "roles/storage.admin"

  members = [
    "serviceAccount:${google_service_account.demosSA.email}"
  ]
}

/*
The next configuration allows demos_sa SA to assume roles or perform actions on behalf of the
default Compute Engine service account (DCESA). This can be useful in scenarios where you want to
delegate certain permissions or actions that typically require the default service account,
without granting broad access or using the default service account directly.

In this particular case, if you don't gran the role serviceAccountUser to demos_sa, the following error is promted:

User (demos_sa) not authorized to act as service account  '321560073577-compute@developer.gserviceaccount.com' (default DCESA).
To act as a service account, user must have one of [Owner, Editor, Service Account Actor] roles.
*/

resource "google_service_account_iam_member" "demosSA_serviceAccountUser_DCESA" {
  service_account_id = data.google_compute_default_service_account.DCESA.name
  role               = "roles/iam.serviceAccountUser"
  member             = "serviceAccount:${google_service_account.demosSA.email}"
}