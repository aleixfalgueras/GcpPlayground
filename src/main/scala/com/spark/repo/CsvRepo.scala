package com.spark.repo
import com.spark.repo.CsvRepo.defaultOptions
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

class CsvRepo(path: String, options: Map[String, String] = defaultOptions)
             (implicit spark: SparkSession) extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading CSV from $path...")
    spark.read.options(options).csv(path)

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    data.write.mode(saveMode).csv(path)
    logger.info(s"CSV write performed in $path (no guarantee of success)")
  }

}

object CsvRepo {

  // Spark default value for "header" is "false"
  val defaultOptions: Map[String, String] = Map(
    "sep" -> ",",
    "header" -> "true",
    "inferSchema" -> "false",
    "dateFormat" -> "yyyy-MM-dd",
    "timestampFormat" -> "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]",
    "encoding" -> "UTF-8"
  )

}
