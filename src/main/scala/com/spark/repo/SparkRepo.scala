package com.spark.repo

import com.spark.repo.implementation.{AvroRepo, BqRepo, CsvRepo, ParquetRepo}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

trait SparkRepo {

  def read(): DataFrame

  def write[T](data: Dataset[T], saveMode: SaveMode): Unit

}

object SparkRepo {

  def getGcsSparkRepo(sparkRepoType: SparkRepoType.Value,
                      gcsPath: String,
                      schema: Option[StructType] = None,
                      readOptions: Option[Map[String, String]] = None,
                      writeOptions: Option[Map[String, String]] = None)
                     (implicit spark: SparkSession): SparkRepo = {
    getSparkRepo(
      sparkRepoType = sparkRepoType,
      dataPath = Some(gcsPath),
      isGcsPath = true,
      schema = schema,
      readOptions = readOptions,
      writeOptions = writeOptions
    )
  }

  /** Provide more or less arguments depending on the possible spark repo that you expect. */
  def getSparkRepo(sparkRepoType: SparkRepoType.Value,
                   bqTableName: Option[String] = None,
                   gcsTmpBucket: Option[String] = None,
                   schema: Option[StructType] = None,
                   readOptions: Option[Map[String, String]] = None,
                   writeOptions: Option[Map[String, String]] = None,
                   dataPath: Option[String] = None,
                   isGcsPath: Boolean = false)
                  (implicit spark: SparkSession): SparkRepo = {

    def getGcsPath(path: String): String = if (path.startsWith("gs://")) path else s"gs://$path"

    sparkRepoType match {
      case SparkRepoType.BQ =>
        val tableName = bqTableName.getOrElse(
          throw new IllegalArgumentException(s"bqTableName parameter must be provided for ${SparkRepoType.BQ} " +
            s"spark repo type")
        )
        gcsTmpBucket match {
          case Some(gcsTempBucket) => new BqRepo(tableName, gcsTempBucket)
          case None => new BqRepo(tableName)
        }

      case SparkRepoType.CSV | SparkRepoType.AVRO | SparkRepoType.PARQUET =>
        val path = dataPath match {
          case Some(path) => if (isGcsPath) getGcsPath(path) else path
          case None => throw new IllegalArgumentException("dataPath parameter must be provided for " +
            s"${SparkRepoType.CSV}, ${SparkRepoType.AVRO} and ${SparkRepoType.PARQUET} spark repo types")
        }
        sparkRepoType match {
          case SparkRepoType.CSV => new CsvRepo(path, schema, readOptions, writeOptions)
          case SparkRepoType.AVRO => new AvroRepo(path)
          case SparkRepoType.PARQUET => new ParquetRepo(path)
        }

      case _ => throw new Exception(s"SparkRepo for $sparkRepoType not implemented")
    }

  }

}
