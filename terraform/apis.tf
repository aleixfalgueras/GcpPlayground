resource "google_project_service" "dataproc_api" {
  service = "dataproc.googleapis.com"
  project = var.project_id
}
