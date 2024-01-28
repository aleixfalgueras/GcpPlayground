## Terraform
Output demos_gcs_sak_out is required to create a secret in GH with the service account key.

## GCP
Project ID: industrial-keep-410516

Buckets:
- gs://aleix-tmp-bucket
- gs://aleix-stg-bucket
- gs://aleix-demos-bucket

Service accounts:
- demos-gcs-sa@industrial-keep-410516.iam.gserviceaccount.com

APIs:
- dataproc.googleapis.com

## GoogleSDKCloud

To install and use GoogleSDKCloud in Windows you must set env var CLOUDSDK_PYTHON (ex: C:\Users\a.falgueras.casals\dev\python\python_3_11_7\python.exe)

Generate application credentials with gcloud auth application-default login and set env var GOOGLE_APPLICATION_CREDENTIALS.
Credentials file can be found here: C:\Users\a.falgueras.casals\AppData\Roaming\gcloud\application_default_credentials.json.
The credentials will apply to all API calls that make use of the Application Default Credentials client library.

### Dataproc
#### Workflow template YAML files
https://cloud.google.com/dataproc/docs/reference/rest/v1/projects.regions.workflowTemplates

**placement.managedCluster.config.gceClusterConfig**

Common config settings for resources of Compute Engine cluster instances, applicable to all instances in the cluster.

**placement.managedCluster.config.gceClusterConfig.serviceAccount: String, Optional.**

The Dataproc service account used by Dataproc cluster VM instances to access Google Cloud Platform services. If not specified, the Compute Engine default service account is used. You can't change the VM service account after the cluster is created

bootDiskSizeGb default is 500GB.

## TODO
- Save SA key in GCS

### TMP

CAUSE: gcloud dataproc workflow-templates instantiate demo1 --region=us-central1

ERROR: (gcloud.dataproc.workflow-templates.instantiate) INVALID_ARGUMENT: User not authorized to act as service account '321560073577-compute@developer.gserviceaccount.com'. To act as a service account, user must have one of [Owner, Editor, Service Account Actor] roles. See https://cloud.google.com/iam/docs/understanding-service-accounts for additional details.

SOLUTION: Grant role iam.serviceAccountUser to the GitHub's SA over the dataproc cluster SA in order to execute the command. If not specified, the Compute Engine default service account is used as dataproc cluster SA, like in this case.

REF: https://stackoverflow.com/questions/63222520/dataproc-operation-failure-invalid-argument-user-not-authorized-to-act-as-serv

The Service Account User role (roles/iam.serviceAccountUser) lets a principal attach a service account to a resource, in order to use it as its default identity. When the code running on that resource needs to authenticate, it can get credentials for the attached service account. 
