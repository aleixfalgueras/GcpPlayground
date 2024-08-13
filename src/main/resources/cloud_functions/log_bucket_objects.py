
from collections import namedtuple
from google.cloud import bigquery

bq_client = bigquery.Client()

# BQ tables
BUCKET_LOG_TABLE = f"testing.BUCKET_LOG"
BUCKET_LOG_SCHEMA = namedtuple('BUCKET_LOG', [
    'NAME',
    'SIZE',
    'DATE'
])


def run_bq_query(query):
    print(f"Running query: {query}")
    return bq_client.query(query).result()


def show_query_results(query):
    df = run_bq_query(query).to_dataframe()
    print(df)


def write_missing_source():
    log_row = BUCKET_LOG_SCHEMA(NAME="", SIZE="nar_id", DATE="")
    log_row_dict = [log_row._asdict()]

    errors = bq_client.insert_rows_json(BUCKET_LOG_TABLE, log_row_dict)
    if not errors:
        print(f"New row has been added in {BUCKET_LOG_TABLE}: \n{log_row_dict}")
        return True
    else:
        print(f"Encountered errors while inserting row: {errors}")
        return False
