package com.spark.repo

import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}


class ParquetRepo(val path: String)(implicit spark: SparkSession) extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading parquet from $path...")
    spark.read.parquet(path)

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    data.write.mode(saveMode).parquet(path)
    logger.info(s"PARQUET write performed in $path (no guarantee of success)")
  }

}
