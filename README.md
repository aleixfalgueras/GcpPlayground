# GCP Playground

Personal project to learn and explore GCP with Scala.

Project Structure:
- JDK Temurin 11.0.24
- Scala 2.12.18

GCP Quotas:
- CPUS ALL REGIONS: 12
- N2 CPUS: 8

## Demos

Demonstrations of use of GCP services and Spark with Scala.

Space use in GCS:
- sellers: 1 KB
- products: 779 MB 
- sales: 10.2 GB

### Dataflow

SCIO plugin is required: https://github.com/spotify/scio-idea-plugin

Add the following options to the IntelliJ Scala compiler and to the SBT VM parameters:

-Dbigquery.project=gcpplayground-433406 -Dbigquery.cache.enabled=false -Dbigquery.types.debug=false

### About SparkExercisesEtl timezone

If you want to use another timezone, modify the current value in the config file (spark_exercises_etl.conf), 
the workflowtemplate being used, and in the initialization action that the Dataproc cluster executes.

code -> spark session timezone (used for internal Spark SQL and Dataframe operations)
workflowtemplate -> JVM timezone (used for logging, if not informed Spark JVM uses the system timezone)
initialization action -> cluster/system timezone (for coherence, not really needed)