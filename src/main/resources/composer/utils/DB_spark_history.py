from airflow import models
from airflow.providers.google.cloud.operators.dataproc import DataprocCreateClusterOperator, ClusterGenerator, \
    DataprocDeleteClusterOperator
from airflow.utils.trigger_rule import TriggerRule
from airflow.utils.dates import days_ago
from airflow.models import Variable

## Variables from Cloud Composer
project_id                 = Variable.get("PROJECT_ID")
region                     = Variable.get("REGION")
dataproc_subnet            = Variable.get("DATAPROC_SUBNET")
gcs_dataproc_staging       = Variable.get("GCS_PWCCLAKE_ES_DATAPROC_STAGING")
gcs_dataproc_temp          = Variable.get("GCS_PWCCLAKE_ES_DATAPROC_TEMP")
gcs_spark_history          = Variable.get("GCS_SPARK_HISTORY")
sa_dataproc                = Variable.get("SA_DATAPROC")
dataproc_kms               = Variable.get("DATAPROC_KEY")
template_id                = "util-spark-history-create-template"
dag_id                     = "util_spark_history_create_{{VERSION}}"

default_args = {
    "start_date": days_ago(1),   # Tell airflow to start one day ago, so that it runs as soon as you upload it
    "project_id": project_id,
    "retries": 0,
}

with models.DAG(
        dag_id            = dag_id,
        default_args      = default_args,
        schedule_interval = '@once',  # The interval with which to schedule the DAG
        max_active_runs   = 1,
        tags              = ["util", "dataproc", "spark", "history-server"]
) as dag:

    ## Cluster configuration
    def config_cluster():
        cluster_config = ClusterGenerator(
            project_id               = project_id,
            region                   = region,
            image_version            = "2.0.56-debian10",
            master_machine_type      = "n2-standard-2",
            idle_delete_ttl          = 36000,
            auto_delete_ttl          = 36000,
            num_workers              = 0,
            storage_bucket           = gcs_dataproc_temp,
            service_account          = sa_dataproc,
            subnetwork_uri           = dataproc_subnet,
            customer_managed_key     = dataproc_kms,
            internal_ip_only         = True,
            enable_component_gateway = True,
            properties = {
                "dataproc:dataproc.allow.zero.workers": "true",
                "yarn:yarn.nodemanager.remote-app-log-dir": f"gs://{gcs_spark_history}/*/*/yarn-logs",
                "spark:spark.history.fs.logDirectory": f"gs://{gcs_spark_history}/*/*/spark-job-history",
                "spark:spark.history.custom.executor.log.url.applyIncompleteApplication": "false",
                "spark:spark.history.custom.executor.log.url": "{{ '{{YARN_LOG_SERVER_URL}}/{{NM_HOST}}:{{NM_PORT}}/{{CONTAINER_ID}}/{{CONTAINER_ID}}/{{USER}}/{{FILE_NAME}}' }}"
            }
        ).make()
        cluster_config['temp_bucket'] = gcs_dataproc_temp
        cluster_config['gce_cluster_config']['shielded_instance_config'] = {
            'enable_secure_boot': True,
            'enable_vtpm': True,
            'enable_integrity_monitoring': True
        }
        cluster_config['gce_cluster_config']['metadata'] = {
            'block-project-ssh-keys': 'true'
        }
        print("############## Cluster configuration ##############")
        print(cluster_config)
        return cluster_config

    ## DAG steps
    create_spark_history = DataprocCreateClusterOperator(
        task_id="create_cluster_for_spark_history",
        project_id=project_id,
        region=region,
        cluster_name="spark-history",
        cluster_config=config_cluster(),
        owner='dataproc'
    )

    delete_spark_history = DataprocDeleteClusterOperator(
        trigger_rule= TriggerRule.ONE_FAILED,
        task_id="delete_cluster_for_spark_history",
        project_id=project_id,
        region=region,
        cluster_name="spark-history",
        owner='dataproc'
    )

    create_spark_history >> delete_spark_history