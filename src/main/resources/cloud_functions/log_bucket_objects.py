
from collections import namedtuple
from google.cloud import bigquery

bq_client = bigquery.Client()

# BQ tables
BUCKET_LOG_TABLE = f"testing.BUCKET_LOG"
BUCKET_LOG_SCHEMA = namedtuple('BUCKET_LOG', [
    'BUCKET_NAME',
    'OBJECT_NAME',
    'SIZE',
    'CONTENT_TYPE',
    'TIME_CREATED',
    'MEDIA_LINK'
])


def write_bucket_log_to_bq(data, context):
    """ Triggered by a change to a Cloud Storage bucket.
    Args:
         data (dict): The Cloud Functions event payload.
         context (google.cloud.functions.Context): Metadata for the event.
    """
    log_row = BUCKET_LOG_SCHEMA(BUCKET_NAME=data['bucket'],
                                OBJECT_NAME=data['name'],
                                SIZE=data.get('size', 0),
                                CONTENT_TYPE=data['contentType'],
                                TIME_CREATED=data['timeCreated'],
                                MEDIA_LINK=data['mediaLink'])
    log_row_dict = [log_row._asdict()]

    errors = bq_client.insert_rows_json(BUCKET_LOG_TABLE, log_row_dict)
    if not errors:
        print(f"New row has been added in {BUCKET_LOG_TABLE}: \n{log_row_dict}")
        return True
    else:
        print(f"Encountered errors while inserting row: {errors}")
        return False
