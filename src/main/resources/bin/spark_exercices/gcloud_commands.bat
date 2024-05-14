REM Util gcloud commands for the project

REM creates spark-exercices-etl-persistent cluster
gcloud dataproc clusters create spark-exercices-etl-persistent --region=us-central1 --enable-component-gateway --bucket=dataproc-stg-bucket --temp-bucket=dataproc-tmp-bucket --image-version=2.2.2-debian12 --master-machine-type=e2-standard-2 --master-boot-disk-size=100 --num-masters=1 --worker-machine-type=e2-standard-2 --worker-boot-disk-size=100 --num-workers=2 --initialization-actions=gs://aleix-demos-bucket/init_actions/set_timezone.sh --initialization-action-timeout=300s
REM deletes spark-exercices-etl-persistent cluster
gcloud dataproc clusters delete spark-exercices-etl-persistent --region=us-central1 --quiet

REM upload dataproc init actions
gsutil cp src/main/resources/bin/set_timezone.sh gs://aleix-demos-bucket/init_actions/