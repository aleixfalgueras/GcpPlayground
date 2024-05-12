package com.spark.sparkrepo

import com.spark.sparkrepo.gcssparkrepo.{AvroGcsSparkRepo, ParquetGcsSparkRepo}
import org.apache.spark.sql.SparkSession

abstract class GcsSparkRepo(val path: String) extends SparkRepo

object GcsSparkRepo {

  def getGcsSparkRepo(sparkRepoType: SparkRepoType.Value, path: String)(implicit spark: SparkSession): GcsSparkRepo = {
    val formattedGcsPath = if (path.startsWith("gs://")) path else s"gs://$path"

    sparkRepoType match {
      case SparkRepoType.`avro` => new AvroGcsSparkRepo(formattedGcsPath)
      case SparkRepoType.`parquet` => new ParquetGcsSparkRepo(formattedGcsPath)
    }
  }

}