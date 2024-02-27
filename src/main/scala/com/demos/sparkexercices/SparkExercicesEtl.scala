package com.demos.sparkexercices

import com.configs.SparkExercicesEtlConfig
import com.demos.utils.StandardCmdLineArgs
import com.demos.utils.PureConfigUtils.readConfigFromFile
import com.spark.SparkSessionUtils.getSparkSession
import com.spark.ExecutionMode
import com.spark.SparkIO.readFromGcs
import org.apache.log4j.Logger
import pureconfig.generic.auto._


object SparkExercicesEtl {
  private val logger: Logger = Logger.getLogger(getClass)

  // adding type annotation crash the code
  implicit val sparkExercicesEtlConfigReader = pureconfig.ConfigReader[SparkExercicesEtlConfig]
  val configFilePath = "config/spark_exercices_etl.conf"

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new StandardCmdLineArgs(args)
    val executionMode = if (argsParsed.executionMode() == "local") ExecutionMode.local else ExecutionMode.GCP

    val sparkExercicesConfig = readConfigFromFile(argsParsed.env(), configFilePath)
    logger.info(sparkExercicesConfig.toString)

    val spark = getSparkSession("SparkExercices", executionMode)
    val df = readFromGcs(spark, sparkExercicesConfig.sellersPath)

    df.cache()
    logger.info(df.count())
    df.show()

  }

}
