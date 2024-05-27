
resource "google_artifact_registry_repository" "artifact_docker_repository" {
  location = var.region
  repository_id = "docker-repo"
  format = "DOCKER"
  description = "Docker repository of the project"

  cleanup_policies {
    id     = "keep-one-version"
    action = "KEEP"
    most_recent_versions {
      keep_count = 1
    }
  }

}
