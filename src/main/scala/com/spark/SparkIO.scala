package com.spark

import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, SparkSession}


object SparkIO  {
  private val logger: Logger = Logger.getLogger(getClass)

  def readFromGcs(spark: SparkSession, gcsPath: String): DataFrame = {
    logger.info(s"Reading $gcsPath")
    spark.read.parquet(gcsPath)

  }

  def readFromBq(spark: SparkSession, tableName: String): DataFrame = {
    logger.info(s"Reading $tableName from BigQuery")
    spark.read.format("bigquery").load(tableName)

  }

  def writeInBq(df: DataFrame, tableName: String): Unit = {
    logger.info(s"Writing $tableName in BigQuery")
    df.write
      .format("bigquery")
      .option("writeMethod", "direct")
      .save(tableName)

  }

}
