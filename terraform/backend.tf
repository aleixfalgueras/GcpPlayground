
terraform {
  // TODO: Change name to terraform-bucket-${var.project_suffix}
  backend "gcs" {
    bucket  = "gcpplay-terraform-bucket"
    prefix  = "terraform/state"
  }
}
