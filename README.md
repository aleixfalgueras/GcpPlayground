## GoogleSDKCloud

To install and use GoogleSDKCloud in Windows you must set env var CLOUDSDK_PYTHON (ex: C:\Users\a.falgueras.casals\dev\python\python_3_11_7\python.exe)

Generate application credentials with gcloud auth application-default login and set env var GOOGLE_APPLICATION_CREDENTIALS.
Credentials file can be found here: C:\Users\a.falgueras.casals\AppData\Roaming\gcloud\application_default_credentials.json. 
The credentials will apply to all API calls that make use of the Application Default Credentials client library.

## Dataproc 
### Workflow template YAML files
https://cloud.google.com/dataproc/docs/reference/rest/v1/projects.regions.workflowTemplates

GceClusterConfig: Common config settings for resources of Compute Engine cluster instances, applicable to all instances in the cluster.

### Vars
Project ID: industrial-keep-410516

### GCP
Buckets:
- gs://aleix-tmp-bucket
- gs://aleix-stg-bucket
- gs://aleix-demos-bucket

Service accounts:
- demos-gcs-sa@industrial-keep-410516.iam.gserviceaccount.com

APIs:
- dataproc.googleapis.com

Permission:
- gsutil iam ch serviceAccount:demos-gcs-sa@industrial-keep-410516.iam.gserviceaccount.com:roles/storage.admin gs://[BUCKET_NAME]
- gsutil iam ch serviceAccount:demos-gcs-sa@industrial-keep-410516.iam.gserviceaccount.com:roles/dataproc.editor

### TODO:
- Save SA key in GCS