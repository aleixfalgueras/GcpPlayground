package com.spark

import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, SparkSession}


object SparkGCS  {
  private val logger: Logger = Logger.getLogger(getClass)

  def readFromGcs(spark: SparkSession, gcsPath: String): DataFrame = {
    logger.info(s"Reading $gcsPath")
    spark.read.parquet(gcsPath)

  }

}
