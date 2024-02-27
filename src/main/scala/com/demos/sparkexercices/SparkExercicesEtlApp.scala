package com.demos.sparkexercices

import com.configs.SparkExercicesEtlConfig
import com.demos.sparkexercices.SparkExercicesEtl.{etlProducts, etlSales, etlSellers}
import com.demos.utils.PureConfigUtils.readConfigFromFile
import com.demos.utils.StandardArgs
import com.spark.ExecutionMode
import com.spark.SparkSessionUtils.getSparkSession
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import pureconfig.generic.auto._

/**
 * Simple Spark-Dataproc ETL example.
 *
 * #dataset #gcs #bigquery
 */
object SparkExercicesEtlApp {
  private val logger: Logger = Logger.getLogger(getClass)

  // adding type annotation crash the code
  implicit val sparkExercicesEtlConfigReader = pureconfig.ConfigReader[SparkExercicesEtlConfig]
  val configFilePath = "config/spark_exercices_etl.conf"

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new SparkExercicesEtlArgs(args)
    val executionMode = if (argsParsed.executionMode() == "local") ExecutionMode.local else ExecutionMode.GCP

    implicit val spark: SparkSession = getSparkSession("SparkExercicesEtl", executionMode)
    implicit val config: SparkExercicesEtlConfig = readConfigFromFile(argsParsed.env(), configFilePath)
    logger.info(config.toString)

    logger.info(s"Executing ${argsParsed.etl()} ETL...")

    argsParsed.etl() match {
      case "sellers" => etlSellers()
      case "products" => etlProducts()
      case "sales" => etlSales()
      case "all" => etlSellers(); etlProducts(); etlSales()
      case _ => throw new Exception(s"ETL ${argsParsed.etl()} not known")
    }

  }

}
