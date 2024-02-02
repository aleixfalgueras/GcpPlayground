resource "google_storage_bucket" "stg_bucket" {
  name = "aleix-stg-bucket"
  location = var.multiregion
}

resource "google_storage_bucket" "tmp_bucket" {
  name = "aleix-tmp-bucket"
  location = var.multiregion
}

resource "google_storage_bucket" "demos_bucket" {
  name = "aleix-demos-bucket"
  location = var.multiregion
}