
resource "google_project_service" "compute_api" {
  service = "compute.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "dataproc_api" {
  service = "dataproc.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "artifactregistry_api" {
  service = "artifactregistry.googleapis.com"
  project = var.project_id
}

# how to fuck yourself: active this API and get extra IAM errors :)
resource "google_project_service" "cloudresource_api" {
  service = "cloudresourcemanager.googleapis.com"
  project = var.project_id
}

# in order to view the monitoring API scope in OAuth 2.0 Playground
resource "google_project_service" "monitoring_api" {
  service = "monitoring.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "dataflow_api" {
  service = "dataflow.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "cloudfunctions_api" {
  service = "cloudfunctions.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "cloudbuild_api" {
  service = "cloudbuild.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "logging_api" {
  service = "logging.googleapis.com"
  project = var.project_id
}

resource "google_project_service" "pubsub_api" {
  service = "pubsub.googleapis.com"
  project = var.project_id
}

