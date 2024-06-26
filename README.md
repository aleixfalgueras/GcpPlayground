## GCP Playground

Personal project to learn and explore GCP.

Quotas:
- CPUS ALL REGIONS: 12
- N2 CPUS: 8

### Dataflow

SCIO plugin is required: https://github.com/spotify/scio-idea-plugin

Add the following options to the IntelliJ Scala compiler and to the SBT VM parameters: 

-Dbigquery.project=industrial-keep-410516 -Dbigquery.cache.enabled=false -Dbigquery.types.debug=false

### Demos (WIP 🔨)

Disk usage in GCS: 
    sellers: 1 KB
    products: 779 MB
    sales: 10.2 GB

#### SparkExercisesEtl timezone

If you want to use another timezone, modify the current value in the config file (spark_exercises_etl.conf), 
the workflowtemplate being used, and in the initialization action that the Dataproc cluster executes.

code -> spark session timezone (used for internal Spark SQL and Dataframe operations)
workflowtemplate -> JVM timezone (used for logging, if not informed Spark JVM uses the system timezone)
initialization action -> cluster/system timezone (for coherence, not really needed)