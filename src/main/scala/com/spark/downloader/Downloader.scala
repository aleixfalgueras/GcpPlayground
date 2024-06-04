package com.spark.downloader

import com.demos.utils.ExecutionMode
import com.spark.utils.SparkSessionUtils.getSparkSession
import com.spark.repo.SparkRepo.getSparkRepo
import com.spark.repo.SparkRepoType
import org.apache.log4j.Logger
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * Download data in a specific GCS or local path from a BQ table or a GCS path in a particular format using Spark.
 *
 * The following example download a csv file from the BQ source table projectid.datasetY.tableX:
 * --sourceRepo=bq --source=projectid.datasetY.tableX --targetRepo=csv --targetPath=gs://some/path/
 */
object Downloader {

  private val logger: Logger = Logger.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new DownloaderArgs(args)
    val source = argsParsed.source.toOption
    val sourceRepoType = SparkRepoType(argsParsed.sourceRepo())
    val targetRepoType = SparkRepoType(argsParsed.targetRepo())
    val targetPath = argsParsed.targetPath()

    implicit val spark: SparkSession = getSparkSession("Downloader", ExecutionMode.local)

    val sourceRepo = getSparkRepo(
      sparkRepoType = sourceRepoType,
      bqTableName = source,
      dataPath = source,
      isGcsPath = true
    )

    val targetRepo = getSparkRepo(
      sparkRepoType = targetRepoType,
      dataPath = Some(targetPath),
      isGcsPath = targetPath.startsWith("gs://")
    )

    logger.info(s"Reading from ${source.get} (repo type: $sourceRepoType, filter: ${argsParsed.whereFilter.toOption}) and " +
      s"writing in $targetPath with $targetRepoType repo type")

    val dataToWrite = argsParsed.whereFilter.toOption match {
      case Some(whereFilter) => sourceRepo.read().where(whereFilter)
      case None => sourceRepo.read()
    }

    targetRepo.write(dataToWrite, SaveMode.Overwrite)

  }

}
