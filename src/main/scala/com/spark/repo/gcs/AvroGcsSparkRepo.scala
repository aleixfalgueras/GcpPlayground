package com.spark.repo.gcs

import com.spark.repo.GcsSparkRepo
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

class AvroGcsSparkRepo(val gcsPath: String)(implicit spark: SparkSession)  extends GcsSparkRepo(gcsPath) {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading AVRO from $gcsPath...")
    spark.read.format("avro").load(gcsPath)

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    data.write.format("avro").mode(saveMode).save(gcsPath)
    logger.info(s"AVRO write performed in $gcsPath (no guarantee of success)")

  }


}