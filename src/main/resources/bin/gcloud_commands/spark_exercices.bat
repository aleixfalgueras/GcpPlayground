REM creates spark-exercises-etl-persistent cluster
gcloud dataproc clusters create spark-exercises-etl-persistent --region=us-central1 --enable-component-gateway --bucket=dataproc-stg-bucket --temp-bucket=dataproc-tmp-bucket --image-version=2.2.2-debian12 --master-machine-type=e2-standard-2 --master-boot-disk-size=100 --num-masters=1 --worker-machine-type=e2-standard-2 --worker-boot-disk-size=100 --num-workers=2 --initialization-actions=gs://aleix-demos-bucket/init_actions/set_timezone.sh --initialization-action-timeout=300s --properties=yarn:yarn.nodemanager.remote-app-log-dir=gs://spark-history-bucket/spark-exercises-etl-persistent/yarn-logs,spark:spark.history.fs.logDirectory=gs://spark-history-bucket/spark-exercises-etl-persistent/spark-job-history,spark:spark.history.custom.executor.log.url.applyIncompleteApplication=false,spark:spark.history.custom.executor.log.url="{{'{{YARN_LOG_SERVER_URL}}/{{NM_HOST}}:{{NM_PORT}}/{{CONTAINER_ID}}/{{CONTAINER_ID}}/{{USER}}/{{FILE_NAME}}' }}"

REM deletes spark-exercises-etl-persistent cluster
gcloud dataproc clusters delete spark-exercises-etl-persistent --region=us-central1 --quiet

REM upload dataproc init actions
gsutil cp src/main/resources/bin/set_timezone.sh gs://aleix-demos-bucket/init_actions/
