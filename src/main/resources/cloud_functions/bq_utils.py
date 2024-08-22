from google.cloud import bigquery

bq_client = bigquery.Client()

def run_bq_query(query):
    print(f"Running query: {query}")
    return bq_client.query(query).result()


def show_query_results(query):
    df = run_bq_query(query).to_dataframe()
    print(df)