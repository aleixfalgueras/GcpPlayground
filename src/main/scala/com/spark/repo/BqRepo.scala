package com.spark.repo
import com.spark.repo.BqRepo.ONLY_READ_REPO
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

import scala.util.{Success, Try, Failure}

class BqRepo(val tableName: String, val gcsTmpBucket: String = ONLY_READ_REPO)(implicit spark: SparkSession)
  extends SparkRepo {

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

  def readExternalTable(): DataFrame = {
    spark.conf.set("materializationDataset", tableName.split('.')(1))
    val query = s"SELECT * FROM `$tableName`"

    Try {
      logger.info(s"Reading from external table $tableName with query $query...")
      spark.read
        .format("bigquery")
        .options(Map("viewsEnabled" -> "true", "query" -> query))
        .load()
    } match {
      case Failure(exc) => logger.error(s"Error trying to read external table $tableName"); throw exc;
      case Success(df) => df
    }

  }

}

object BqRepo {

  val ONLY_READ_REPO = "ONLY_READ_REPO"

}
