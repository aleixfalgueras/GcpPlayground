import os

from collections import namedtuple
from google.cloud import bigquery

# Environment variables
GCP_PROJECT = os.environ.get("GCP_PROJECT")

# BQ tables
BUCKET_LOG_TABLE = f"{GCP_PROJECT}.testing.BUCKET_LOG"
BUCKET_LOG_NAMES = namedtuple('BUCKET_LOG', [
    'BUCKET_NAME',
    'OBJECT_NAME',
    'CONTENT_TYPE',
    'TIME_CREATED'
])

bq_client = bigquery.Client()


def write_bucket_log_to_bq(event):
    """ Triggered by a change to a Cloud Storage bucket.
    Args:
         event: cloudevents.http.event.CloudEvent
    """
    data = event.data
    log_row = BUCKET_LOG_NAMES(BUCKET_NAME=data['bucket'],
                               OBJECT_NAME=data['name'],
                               CONTENT_TYPE=data['contentType'],
                               TIME_CREATED=data['timeCreated'])
    log_row_dict = [log_row._asdict()]

    errors = bq_client.insert_rows_json(BUCKET_LOG_TABLE, log_row_dict)
    if not errors:
        print(f"New row has been added in {BUCKET_LOG_TABLE}: \n{log_row_dict}")
        return True
    else:
        print(f"Encountered errors while inserting row: {errors}")
        return False
