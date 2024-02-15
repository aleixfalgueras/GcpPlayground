import logging
import sys

from airflow.models import Variable
from google.cloud import storage

# airflow variables

PROJECT_ID = Variable.get("PROJECT_ID")
REGION = Variable.get("REGION")
DATAPROC_STG_BUCKET = Variable.get("DATAPROC_STG_BUCKET")
DATAPROC_TMP_BUCKET = Variable.get("DATAPROC_TMP_BUCKET")
SPARK_HISTORY_BUCKET = Variable.get("SPARK_HISTORY_BUCKET")


def get_spark_history_cluster_config(project_id=PROJECT_ID,
                                     region=REGION,
                                     spark_history_bucket=SPARK_HISTORY_BUCKET,
                                     dataproc_stg_bucket=DATAPROC_STG_BUCKET,
                                     dataproc_tmp_bucket=DATAPROC_TMP_BUCKET,
                                     delete_ttl=36000,  # 10 h
                                     master_machine_type="n2-standard-2",
                                     image="2.0.56-debian10"):
    cluster_config = ClusterGenerator(
        project_id=project_id,
        region=region,
        image_version=image,
        master_machine_type=master_machine_type,
        idle_delete_ttl=delete_ttl,
        auto_delete_ttl=delete_ttl,
        num_workers=0,
        storage_bucket=dataproc_stg_bucket,
        enable_component_gateway=True,
        properties={
            "yarn:yarn.nodemanager.remote-app-log-dir": f"gs://{spark_history_bucket}/*/*/yarn-logs",
            "spark:spark.history.fs.logDirectory": f"gs://{spark_history_bucket}/*/*/spark-job-history",
            "spark:spark.history.custom.executor.log.url.applyIncompleteApplication": "false",
            "spark:spark.history.custom.executor.log.url": "{{ '{{YARN_LOG_SERVER_URL}}/{{NM_HOST}}:{{NM_PORT}}/{{CONTAINER_ID}}/{{CONTAINER_ID}}/{{USER}}/{{FILE_NAME}}' }}"
        }
    ).make()

    cluster_config['temp_bucket'] = dataproc_tmp_bucket

    return cluster_config


def read_text_from_gcs(bucket_name, file_path):
    logging.info(f"Trying to read {bucket_name}/{file_path}")

    blob_text = storage \
        .Client(project=project_id) \
        .get_bucket(bucket_name) \
        .get_blob(file_path) \
        .download_as_text(encoding="utf-8'")

    logging.info("Content: \n" + blob_text)

    return blob_text


def get_dataproc_import_bash_commands(workflow_template_filename,
                                      workflow_template_id,
                                      workflow_template_file_path,
                                      process_log_spark_history_path,
                                      tmp_local_dir_path="./tmp"):
    """
    Get the bash commnands that will be run to modify and import the workflow_template_file to dataproc.

    ! workflow_template_id must match regex -> [a-z]([-a-z0-9]{0,34}[a-z0-9])

    Important: path params project_bucket_path and process_spark_history_log_path must haven't
        leading or trailing forward slash. Ex: "sdda/accounts".

    :param workflow_template_filename: name of the workflow template file to import
    :param workflow_template_id: workflow template id and cluster name
    :param workflow_template_file_path: workflow template path in GCS
    :param process_log_spark_history_path: custom path for the process' spark history logs
    :param tmp_local_dir_path: temporal local dir path for downloaded files
    :return: bash commands to be executed in string format
    """
    logging.info("SYS PATH: \n" + str(sys.path))

    mkdir_tmp_local_command = f"mkdir {tmp_local_dir_path}"
    gs_cp_workflow_template_file_command = f'gsutil cp gs://{workflow_template_file_path} {tmp_local_dir_path}'
    ls_tmp_local_dir_command = f"ls -lhp {tmp_local_dir_path}"

    spark_history_process_logs_path_final = f"{SPARK_HISTORY_BUCKET}/{process_log_spark_history_path}".replace('/', r'\/')
    workflow_template_local_path = f"{tmp_local_dir_path}/{workflow_template_filename}".replace('/', r'\/')

    sed_command = f"sed -i 's/@@CLUSTER_NAME@@/{workflow_template_id}/g; " \
                  f"s/@@DATAPROC_STG_BUCKET@@/{DATAPROC_STG_BUCKET}/g; " \
                  f"s/@@DATAPROC_TMP_BUCKET@@/{DATAPROC_TMP_BUCKET}/g; " \
                  f"s/@@SPARK_HISTORY_BUCKET@@/{spark_history_process_logs_path_final}/g' " \
                  f"{workflow_template_local_path}"

    dataproc_import_command = f'gcloud dataproc workflow-templates import {workflow_template_id} ' \
                              f'--project={PROJECT_ID} --region={REGION} ' \
                              f'--source={workflow_template_local_path} --quiet'

    remove_local_files_command = f'rm {workflow_template_local_path}'

    return f'{mkdir_tmp_local_command} && {gs_cp_workflow_template_file_command} && {ls_tmp_local_dir_command} && ' \
           f'{sed_command} && {dataproc_import_command} && {remove_local_files_command}'
