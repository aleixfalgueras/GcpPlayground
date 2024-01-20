### PowerShell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass (solve ".ps1 is not digitally signed")

### auth
gcloud auth login
gcloud auth application-default login

### config
gcloud config configurations create {configurationName}
gcloud config list
gcloud config set project {projectId}

### gsutil
gsutil [-m] cp {local_path} gs://
gsutil rm [-r] gs://

### storage
gcloud storage buckets create gs://
gcloud storage buckets list
gcloud storage cp {local_path} gs://
gcloud storage rm gs://