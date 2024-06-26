package com.demos.dataproc.etl

import com.configs.SparkEtlConfig
import com.demos.dataproc.etl.SparkEtl.{etlProducts, etlSales, etlSellers}
import com.demos.utils
import com.demos.utils.ExecutionMode
import com.demos.utils.PureConfigUtils.readConfigFromFile
import com.spark.repo.SparkRepo.{getGcsSparkRepo, getSparkRepo}
import com.spark.repo._
import com.spark.repo.implementation.{AvroRepo, CsvRepo, ParquetRepo}
import com.spark.utils.SparkSessionUtils.getSparkSession
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import pureconfig.generic.auto._

/**
 * Simple Spark-Dataproc ETL examples.
 *
 */
object SparkEtlApp {
  private val logger: Logger = Logger.getLogger(getClass)

  // ! adding type annotation crash the code
  implicit val sparkEtlConfigReader = pureconfig.ConfigReader[SparkEtlConfig]
  val configFilePath = "config/spark_etl.conf"

  def updateGcsPathWithFormat(targetRepo: SparkRepo)(implicit spark: SparkSession): SparkRepo = targetRepo match {
    case repo: AvroRepo => new AvroRepo(repo.path + "avro/")
    case repo: ParquetRepo => new ParquetRepo(repo.path + "parquet/")
    case repo: CsvRepo => new CsvRepo(repo.path + "csv/", repo.someSchema, repo.readOptions, repo.writeOptions)
    case _ => targetRepo
  }

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val sparkEtlAppArgs = new SparkEtlAppArgs(args)
    val executionMode = ExecutionMode(sparkEtlAppArgs.executionMode())
    val targetRepoType = SparkRepoType(sparkEtlAppArgs.targetRepo())
    logger.info(s"Execution mode: $executionMode, target repo: $targetRepoType")

    implicit val config: SparkEtlConfig = readConfigFromFile(sparkEtlAppArgs.env(), configFilePath)
    implicit val spark: SparkSession = getSparkSession(s"SparkEtl_${sparkEtlAppArgs.etl()}", executionMode, config.timezone)

    logger.info(config.toString)

    val sellersSourceRepo = getGcsSparkRepo(SparkRepoType.PARQUET, config.sellersSourceGcsPath)
    val productsSourceRepo = getGcsSparkRepo(SparkRepoType.PARQUET, config.productsSourceGcsPath)
    val salesSourceRepo = getGcsSparkRepo(SparkRepoType.PARQUET, config.salesSourceGcsPath)

    val sellersTargetRepo = updateGcsPathWithFormat(getSparkRepo(
      sparkRepoType = targetRepoType,
      bqTableName = Some(config.sellersTable),
      gcsTmpBucket = Some(config.gcp.bqTmpBucket),
      dataPath = Some(config.sellersTargetGcsPath),
      isGcsPath = true
    ))

    val productsTargetRepo = updateGcsPathWithFormat(getSparkRepo(
      sparkRepoType = targetRepoType,
      bqTableName = Some(config.productsTable),
      gcsTmpBucket = Some(config.gcp.bqTmpBucket),
      dataPath = Some(config.productsTargetGcsPath),
      isGcsPath = true
    ))

    val salesTargetRepo = updateGcsPathWithFormat(getSparkRepo(
      sparkRepoType = targetRepoType,
      bqTableName = Some(config.salesTable),
      gcsTmpBucket = Some(config.gcp.bqTmpBucket),
      dataPath = Some(config.salesTargetGcsPath),
      isGcsPath = true
    ))

    logger.info(s"Executing ${sparkEtlAppArgs.etl()} ETL...")
    sparkEtlAppArgs.etl() match {
      case "sellers" => etlSellers(sellersSourceRepo, sellersTargetRepo)
      case "products" => etlProducts(productsSourceRepo, productsTargetRepo)
      case "sales" => etlSales(salesSourceRepo, salesTargetRepo)
      case "all" =>
        executionMode match {
          case utils.ExecutionMode.LOCAL =>
            etlSellers(sellersSourceRepo, sellersTargetRepo)
            etlProducts(productsSourceRepo, productsTargetRepo)
          case utils.ExecutionMode.GCP =>
            etlSellers(sellersSourceRepo, sellersTargetRepo)
            etlProducts(productsSourceRepo, productsTargetRepo)
            etlSales(salesSourceRepo, salesTargetRepo)
        }
      case _ =>
        throw new Exception(s"ETL ${sparkEtlAppArgs.etl()} not known")
    }

    logger.info(s"ETL ${sparkEtlAppArgs.etl()} ($targetRepoType flavour) finished.")

  }

}
