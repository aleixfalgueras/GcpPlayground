package com.demos.sparkexercices

import com.configs.SparkExercicesConfig
import com.demos.utils.ExecutionMode
import com.demos.utils.PureConfigUtils.readConfigFromFile
import com.demos.utils.SparkSessionUtils.getSparkSession
import com.spark.SparkIO.readFromGcs
import org.apache.log4j.Logger
import org.rogach.scallop.{ScallopConf, ScallopOption}
import pureconfig.generic.auto._

class SparkExercicesCmdLineArgs(args: Seq[String]) extends ScallopConf(args) {
  val env: ScallopOption[String] = choice(
    name = "env",
    choices = Seq("dev"),
    required = true,
    descr = "Environment: dev"
  )
  val executionMode: ScallopOption[String] = choice(
    name = "executionMode",
    choices = Seq("local", "GCP"),
    required = true,
    descr = "Execution mode: local, GCP"
  )

  verify()

}

object SparkExercicesEtl {
  private val logger: Logger = Logger.getLogger(getClass)

  // adding type annotation crash the code
  implicit val sparkExercicesConfigReader = pureconfig.ConfigReader[SparkExercicesConfig]

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new SparkExercicesCmdLineArgs(args)
    val EXECUTION_MODE = if (argsParsed.executionMode() == "local") ExecutionMode.local else ExecutionMode.GCP

    val sparkExercicesConfig = readConfigFromFile(argsParsed.env(), "config/spark_exercices.conf")
    logger.info(sparkExercicesConfig.toString)

    val spark = getSparkSession("SparkExercices", EXECUTION_MODE)
    val df = readFromGcs(spark, sparkExercicesConfig.sellersPath)

    df.cache()
    logger.info(df.count())
    df.show()

  }

}
