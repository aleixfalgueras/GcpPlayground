package com.spark.sparkrepo.gcssparkrepo

import com.spark.sparkrepo.GcsSparkRepo
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}


class ParquetGcsSparkRepo(val gcsPath: String)(implicit spark: SparkSession) extends GcsSparkRepo(gcsPath) {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading parquet from $path...")
    spark.read.parquet(path)

  }

  override def write[T](data: Dataset[T], mode: SaveMode): Unit = {
    data.write.mode(mode).parquet(path)
    logger.info(s"PARQUET write performed in $path (no guarantee of success)")
  }

}
