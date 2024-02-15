from airflow import models
from airflow.providers.google.cloud.operators.dataproc import DataprocCreateClusterOperator, DataprocDeleteClusterOperator
from airflow.utils.dates import days_ago
from airflow.utils.trigger_rule import TriggerRule

from ..utils import *

default_args = {
    "start_date": days_ago(1),  # start one day ago -> it runs as soon as you upload it
    "project_id": PROJECT_ID,
    "retries": 0,
}

with models.DAG(
        dag_id="util_spark_history_create_{{VERSION}}",
        default_args=default_args,
        schedule_interval='@once',
        max_active_runs=1
) as dag:
    create_spark_history = DataprocCreateClusterOperator(
        task_id="create_spark_history_cluster",
        project_id=PROJECT_ID,
        region=REGION,
        cluster_name="spark-history",
        cluster_config=get_spark_history_cluster_config(),
        owner='dataproc'
    )

    delete_spark_history = DataprocDeleteClusterOperator(
        trigger_rule=TriggerRule.ONE_FAILED,
        task_id="delete_spark_history_cluster",
        project_id=PROJECT_ID,
        region=REGION,
        cluster_name="spark-history",
        owner='dataproc'
    )

    create_spark_history >> delete_spark_history
