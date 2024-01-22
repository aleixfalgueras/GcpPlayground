### PowerShell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass (solve ".ps1 is not digitally signed")

### auth
gcloud auth login
gcloud auth application-default login

### service accounts
gcloud iam service-accounts create [SERVICE_ACCOUNT_NAME] --display-name "[SERVICE_ACCOUNT_DISPLAY_NAME]"
gcloud iam service-accounts keys create [FILE_PATH] --iam-account [SERVICE_ACCOUNT_NAME]@[PROJECT_ID].iam.gserviceaccount.com
gsutil iam ch serviceAccount:[SERVICE_ACCOUNT_EMAIL]:roles/storage.admin gs://[BUCKET_NAME]

### config
gcloud config configurations create [CONFIGURATION_NAME]
gcloud config list
gcloud config set project [PROJECT_ID]

### gsutil
gsutil [-m] cp [LOCAL_PATH] gs://
gsutil rm [-r] gs://

### storage
gcloud storage buckets create gs://
gcloud storage buckets list
gcloud storage cp [LOCAL_PATH] gs://
gcloud storage rm gs://

### dataproc
gcloud dataproc workflow-templates import [TEMPLATE_ID] --region=[REGION] --source=[YAML_FILE_PATH] (source can't be a GCS path)
gcloud dataproc workflow-templates instantiate [TEMPLATE_ID] --region=[REGION]
