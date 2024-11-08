REM creates spark-etl-persistent cluster: e2-standard-2, 2 W
gcloud dataproc clusters create spark-etl-persistent --region=us-central1 --worker-machine-type=e2-standard-2 --num-workers=2 --worker-boot-disk-size=100  --enable-component-gateway --bucket=dataproc-stg-bucket-gcpplay --temp-bucket=dataproc-tmp-bucket-gcpplay --image-version=2.2.2-debian12 --master-machine-type=e2-standard-2 --master-boot-disk-size=100 --num-masters=1 --initialization-actions=gs://demos-bucket-gcpplay/init_actions/set_timezone.sh --initialization-action-timeout=300s --properties=yarn:yarn.nodemanager.remote-app-log-dir=gs://spark-history-bucket-gcpplay/spark-etl-persistent/yarn-logs,spark:spark.history.fs.logDirectory=gs://spark-history-bucket-gcpplay/spark-etl-persistent/spark-job-history,spark:spark.history.custom.executor.log.url.applyIncompleteApplication=false,spark:spark.history.custom.executor.log.url="{{'{{YARN_LOG_SERVER_URL}}/{{NM_HOST}}:{{NM_PORT}}/{{CONTAINER_ID}}/{{CONTAINER_ID}}/{{USER}}/{{FILE_NAME}}' }}" --max-idle=t30m

REM deletes spark-etl-persistent cluster
gcloud dataproc clusters delete spark-etl-persistent --region=us-central1 --quiet

REM upload dataproc init actions
gsutil cp src/main/resources/bin/set_timezone.sh gs://demos-bucket-gcpplay/init_actions/
