package com.spark.repo

import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

trait SparkRepo {

  def read(): DataFrame

  def write[T](data: Dataset[T], saveMode: SaveMode): Unit

}

object SparkRepo {

  def getGcsSparkRepo(sparkRepoType: SparkRepoType.Value, gcsPath: String)(implicit spark: SparkSession): SparkRepo = {
    getSparkRepo(sparkRepoType, None, None, Some(gcsPath))
  }

  def getSparkRepo(sparkRepoType: SparkRepoType.Value,
                   someTableName: Option[String] = None,
                   someGcsTempBucket: Option[String] = None,
                   somePath: Option[String] = None,
                   gcsPath: Boolean = true)
                  (implicit spark: SparkSession): SparkRepo = {

    def getGcsPath(path: String): String = if (path.startsWith("gs://")) path else s"gs://$path"

    sparkRepoType match {
      case SparkRepoType.bq =>
        val tableName= someTableName.getOrElse(
          throw new IllegalArgumentException("Table name must be provided for BQ spark repo type")
        )
        val gcsTempBucket = someGcsTempBucket.getOrElse(
          throw new IllegalArgumentException("GCS temporal bucket must be provided for BQ spark repo type")
        )
        new BqRepo(tableName, gcsTempBucket)

      case SparkRepoType.csv | SparkRepoType.avro | SparkRepoType.parquet =>
        val path = somePath match {
          case Some(path) => if (gcsPath) getGcsPath(path) else path
          case None => throw new IllegalArgumentException("Path must be provided for " +
            s"${SparkRepoType.csv}, ${SparkRepoType.avro} and ${SparkRepoType.parquet} spark repo types")
        }
        sparkRepoType match {
          case SparkRepoType.csv => new CsvRepo(path)
          case SparkRepoType.avro => new AvroRepo(path)
          case SparkRepoType.parquet => new ParquetRepo(path)
        }

      case _ => throw new Exception(s"SparkRepo for $sparkRepoType not implemented")
    }

  }

}
