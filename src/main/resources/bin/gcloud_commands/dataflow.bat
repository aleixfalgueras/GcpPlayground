REM dataflow hello world template
gcloud dataflow flex-template build gs://demos-bucket-gcpplay/dataflow_templates/dataflow_hello_world_template.json --image-gcr-path "us-central1-docker.pkg.dev/gcpplayground-433406/docker-repo/dataflow-hello-world:latest" --sdk-language "JAVA" --flex-template-base-image JAVA11 --jar "target/scala-2.12/GcpPlayground-assembly-0.1.0-SNAPSHOT.jar" --env FLEX_TEMPLATE_JAVA_MAIN_CLASS="com.demos.quickstart.DataflowHelloWorld" --staging-location "gs://tmp-bucket-gcpplay/dataflow/staging" --temp-location "gs://tmp-bucket-gcpplay/dataflow/tmp" --gcs-log-dir "gs://tmp-bucket-gcpplay/dataflow/cloudbuild_logs"

REM dataflow hello world run
gcloud dataflow flex-template run "hello-world-fat" --template-file-gcs-location "gs://demos-bucket-gcpplay/dataflow_templates/dataflow_hello_world_template.json" --region "us-central1" --staging-location "gs://tmp-bucket-gcpplay/dataflow/staging" --temp-location "gs://tmp-bucket-gcpplay/dataflow/tmp"