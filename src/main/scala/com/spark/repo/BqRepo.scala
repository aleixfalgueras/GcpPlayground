package com.spark.repo
import com.spark.repo.BqRepo.ONLY_READ_REPO
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

class BqRepo(tableName: String, gcsTmpBucket: String = ONLY_READ_REPO)(implicit spark: SparkSession) extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading $tableName from BigQuery...")
    spark.read.format("bigquery").load(tableName)

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    logger.info(s"Writing $tableName in BigQuery...")
    data.write
      .format("bigquery")
      .mode(saveMode)
      .option("temporaryGcsBucket", gcsTmpBucket)
      .save(tableName)

  }

}

object BqRepo {

  val ONLY_READ_REPO = "ONLY_READ_REPO"

}
