package com.spark.repo.implementation

import com.spark.repo.SparkRepo
import com.spark.repo.implementation.CsvRepo.defaultOptions
import org.apache.log4j.Logger
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

class CsvRepo(val path: String,
              val someSchema: Option[StructType] = None,
              val readOptions: Option[Map[String, String]] = Some(defaultOptions),
              val writeOptions: Option[Map[String, String]] = Some(defaultOptions))
             (implicit spark: SparkSession) extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading CSV from $path... (schema: ${someSchema.isDefined})")

    someSchema match {
      case Some(schema) => spark.read.schema(schema).options(readOptions.getOrElse(defaultOptions)).csv(path)
      case None => spark.read.options(readOptions.get).csv(path)
    }

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    data.write.options(writeOptions.getOrElse(defaultOptions)).mode(saveMode).csv(path)
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
