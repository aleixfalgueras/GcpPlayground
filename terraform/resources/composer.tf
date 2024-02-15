# composer required resources

provider "google-beta" {
  project = var.project_id
  region  = var.region
}

resource "google_project_service" "composer_api" {
  provider           = google-beta
  project            = var.project_id
  service            = "composer.googleapis.com"
  // Disabling Cloud Composer API might irreversibly break all other environments in your project.
  // This parameter prevents automatic disabling of the API when the resource is destroyed.
  disable_on_destroy = false
}

// cloudcomposerGSA = cloud composer managed google service account
resource "google_project_iam_member" "cloudcomposerGSA_composer_ServiceAgentV2Ext" {
  provider = google-beta
  project  = var.project_id
  role     = "roles/composer.ServiceAgentV2Ext"
  member   = "serviceAccount:service-${var.project_number}@cloudcomposer-accounts.iam.gserviceaccount.com"
}

resource "google_composer_environment" "composer" {
  provider = google-beta
  name     = "composer"
  region   = var.region

  config {
    software_config {
      image_version = "composer-1.20.12-airflow-2.3.4"
    }
  }
}