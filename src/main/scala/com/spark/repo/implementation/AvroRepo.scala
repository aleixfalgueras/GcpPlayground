package com.spark.repo.implementation

import com.spark.repo.SparkRepo
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

class AvroRepo(val path: String)(implicit spark: SparkSession) extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading AVRO from $path...")
    spark.read.format("avro").load(path)

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    data.write.format("avro").mode(saveMode).save(path)
    logger.info(s"AVRO write performed in $path (no guarantee of success)")

  }


}