import logging
import sys

from airflow.models import Variable
from google.cloud import storage

# airflow global variables
project_id = Variable.get("PROJECT_ID")
region = Variable.get("REGION")
dataproc_subnet = Variable.get("DATAPROC_SUBNET").replace('/', r'\/')
gcs_dataproc_staging = Variable.get("GCS_PWCCLAKE_ES_DATAPROC_STAGING")
gcs_dataproc_temp = Variable.get("GCS_PWCCLAKE_ES_DATAPROC_TEMP")
sa_dataproc = Variable.get("SA_DATAPROC")
dataproc_kms = Variable.get("DATAPROC_KEY").replace('/', r'\/')
gcs_spark_history = Variable.get("GCS_SPARK_HISTORY")


def get_var_gcs_spark_history():
    return gcs_spark_history


# project local variables
project_name = "pwcclakees-data-modelling-dwh"
project_sbt_version = "0.0.1-SNAPSHOT"

# project cloud variables
dwh_bucket = "db-dev-europe-west3-gcs-125479-2-dwh-es"

bucket_version_path = f"{dwh_bucket}/{project_sbt_version}"
dwh_jar_path = f"gs://{bucket_version_path}/jars/{project_name}-assembly-{project_sbt_version}.jar"

dataproc_path = f"{project_sbt_version}/dataproc"
dataproc_config_path = f"{dataproc_path}/config"
workflow_templates_path = f"{dataproc_path}/workflow_templates"

# TODO: SDLAPPES-2305
# PyComposer constants and code
BIN_PATH = "/home/airflow/gcs/dags/common/bin"
CHECK_AND_INIT_GCS_FOLDER_SH_FILENAME = "init_GCS_folder.sh"


def get_check_and_init_gcs_folder_sh_path():
    return f"{BIN_PATH}/{CHECK_AND_INIT_GCS_FOLDER_SH_FILENAME}"


def read_text_from_gcs(bucket_name, file_path):
    logging.info(f"Trying to read {bucket_name}/{file_path}")

    blob_text = storage \
        .Client(project=project_id) \
        .get_bucket(bucket_name) \
        .get_blob(file_path) \
        .download_as_text(encoding="utf-8'")

    logging.info("Content: \n" + blob_text)

    return blob_text


#####################################################################################################

def get_workflow_template_file_path(workflow_template_filename):
    return f"{dwh_bucket}/{workflow_templates_path}/{workflow_template_filename}"


# TODO: Dataproc global config dynamic injection implementation is pending
def get_dataproc_import_bash_commands(project_bucket,
                                      workflow_template_filename,
                                      workflow_template_id,
                                      workflow_template_file_path,
                                      process_log_spark_history_path,
                                      tmp_local_dir_path="./tmp"):
    """
    Get the bash commnands that will be run to modify and import the workflow_template_file to dataproc.

    ! workflow_template_id must match regex -> [a-z]([-a-z0-9]{0,34}[a-z0-9])

    Important: path params project_bucket_path and process_spark_history_log_path must haven't
        leading or trailing forward slash. Ex: "sdda/accounts".

    :param project_bucket: bucket name of the project
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

    spark_history_process_logs_path_final = f"{gcs_spark_history}/{process_log_spark_history_path}".replace('/', r'\/')
    workflow_template_local_path = f"{tmp_local_dir_path}/{workflow_template_filename}".replace('/', r'\/')

    sed_command = f"sed -i 's/@@DATAPROC_SUBNET@@/{dataproc_subnet}/g; " \
                  f"s/@@DATAPROC_KMS@@/{dataproc_kms}/g; " \
                  f"s/@@SA_DATAPROC@@/{sa_dataproc}/g; " \
                  f"s/@@GCS_PWCCLAKE_ES_DATAPROC_STAGING@@/{gcs_dataproc_staging}/g; " \
                  f"s/@@GCS_PWCCLAKE_ES_DATAPROC_TEMP@@/{gcs_dataproc_temp}/g; " \
                  f"s/@@DATAPROC_CLUSTER_NAME@@/{workflow_template_id}/g; " \
                  f"s/@@GCS_PWCCLAKE_ES_DATAPROC_TEMP@@/{gcs_dataproc_temp}/g; " \
                  f"s/@@GCS_SPARK_HISTORY@@/{spark_history_process_logs_path_final}/g' " \
                  f"{workflow_template_local_path}"

    dataproc_import_command = f'gcloud dataproc workflow-templates import {workflow_template_id} ' \
                              f'--project={project_id} --region={region} ' \
                              f'--source={workflow_template_local_path} --quiet'

    remove_local_files_command = f'rm {workflow_template_local_path}'

    return f'{mkdir_tmp_local_command} && {gs_cp_workflow_template_file_command} && {ls_tmp_local_dir_command} && ' \
           f'{sed_command} && {dataproc_import_command} && {remove_local_files_command}'
