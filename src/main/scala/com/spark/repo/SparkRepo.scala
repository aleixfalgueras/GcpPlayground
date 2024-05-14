package com.spark.repo

import com.spark.repo.GcsSparkRepo.getGcsSparkRepo
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

trait SparkRepo {

  def read(): DataFrame

  def write[T](data: Dataset[T], mode: SaveMode): Unit

}

object SparkRepo {

  def getSparkRepo(sparkRepoType: SparkRepoType.Value,
                   someTableName: Option[String] = None,
                   someGcsTempBucket: Option[String] = None,
                   someGcsPath: Option[String] = None)
                  (implicit spark: SparkSession): SparkRepo = {
    sparkRepoType match {
      case SparkRepoType.`bq` =>
        val tableName= someTableName.getOrElse(
          throw new IllegalArgumentException("Table name must be provided for BQ spark repo type")
        )
        val gcsTempBucket = someGcsTempBucket.getOrElse(
          throw new IllegalArgumentException("GCS temporal bucket must be provided for BQ spark repo type")
        )
        new BqSparkRepo(tableName, gcsTempBucket)

      case SparkRepoType.`avro` | SparkRepoType.`parquet` =>
        val path = someGcsPath.getOrElse(
          throw new IllegalArgumentException("GCS path must be provided for AVRO and PARQUET spark repo type")
        )
        getGcsSparkRepo(sparkRepoType, path)

      case _ => throw new Exception(s"SparkRepo for $sparkRepoType not implemented")
    }
  }

}
