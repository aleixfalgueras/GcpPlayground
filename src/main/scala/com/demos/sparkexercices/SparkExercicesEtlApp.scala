package com.demos.sparkexercices

import com.configs.SparkExercicesEtlConfig
import com.demos.sparkexercices.SparkExercicesEtl.{etlProducts, etlSales, etlSellers}
import com.demos.utils.ExecutionMode
import com.demos.utils.PureConfigUtils.readConfigFromFile
import com.spark.SparkSessionUtils.getSparkSession
import com.spark.repo.GcsSparkRepo.getGcsSparkRepo
import com.spark.repo.SparkRepo.getSparkRepo
import com.spark.repo.SparkRepoType.getSparkRepoType
import com.spark.repo.gcs.{AvroGcsSparkRepo, ParquetGcsSparkRepo}
import com.spark.repo.{SparkRepo, SparkRepoType}
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import pureconfig.generic.auto._

/**
 * Simple Spark-Dataproc ETL examples.
 *
 */
object SparkExercicesEtlApp {
  private val logger: Logger = Logger.getLogger(getClass)

  // ! adding type annotation crash the code
  implicit val sparkExercicesEtlConfigReader = pureconfig.ConfigReader[SparkExercicesEtlConfig]
  val configFilePath = "config/spark_exercices_etl.conf"

  def updateGcsPathWithFormat(targetRepo: SparkRepo)(implicit spark: SparkSession): SparkRepo = targetRepo match {
    case repo: AvroGcsSparkRepo => new AvroGcsSparkRepo(repo.path + "avro/")
    case repo: ParquetGcsSparkRepo => new ParquetGcsSparkRepo(repo.path + "parquet/")
    case _ => targetRepo
  }

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new SparkExercicesEtlArgs(args)
    val executionMode = if (argsParsed.executionMode() == "local") ExecutionMode.local else ExecutionMode.GCP
    val targetRepo = getSparkRepoType(argsParsed.targetRepo())
    logger.info(s"Execution mode: $executionMode, target repo: $targetRepo")

    implicit val spark: SparkSession = getSparkSession("SparkExercicesEtl", executionMode)
    implicit val config: SparkExercicesEtlConfig = readConfigFromFile(argsParsed.env(), configFilePath)

    logger.info(config.toString)

    val sellersSourceRepo = getGcsSparkRepo(SparkRepoType.parquet, config.sellersSourceGcsPath)
    val productsSourceRepo = getGcsSparkRepo(SparkRepoType.parquet, config.productsSourceGcsPath)
    val salesSourceRepo = getGcsSparkRepo(SparkRepoType.parquet, config.salesSourceGcsPath)

    val someGcsTmpBucket = Some(config.gcp.bqTmpBucket)

    val sellersTargetRepo = updateGcsPathWithFormat(getSparkRepo(targetRepo,
      Some(config.sellersTable),
      someGcsTmpBucket,
      Some(config.sellersTargetGcsPath)
    ))

    val productsTargetRepo = updateGcsPathWithFormat(getSparkRepo(
      targetRepo,
      Some(config.productsTable),
      someGcsTmpBucket,
      Some(config.productsTargetGcsPath)
    ))

    val salesTargetRepo = updateGcsPathWithFormat(getSparkRepo(targetRepo,
      Some(config.salesTable),
      someGcsTmpBucket,
      Some(config.salesTargetGcsPath)
    ))

    logger.info(s"Executing ${argsParsed.etl()} ETL...")
    argsParsed.etl() match {
      case "sellers" => etlSellers(sellersSourceRepo, sellersTargetRepo)
      case "products" => etlProducts(productsSourceRepo, productsTargetRepo)
      case "sales" => etlSales(salesSourceRepo, salesTargetRepo)
      case "all" =>
        executionMode match {
          case com.demos.utils.ExecutionMode.local =>
            etlSellers(sellersSourceRepo, sellersTargetRepo)
            etlProducts(productsSourceRepo, productsTargetRepo)
          case com.demos.utils.ExecutionMode.GCP =>
            etlSellers(sellersSourceRepo, sellersTargetRepo)
            etlProducts(productsSourceRepo, productsTargetRepo)
            etlSales(salesSourceRepo, salesTargetRepo)
        }
      case _ =>
        throw new Exception(s"ETL ${argsParsed.etl()} not known")
    }

    logger.info(s"ETL ${argsParsed.etl()} ($targetRepo flavour) finished.")

  }

}
