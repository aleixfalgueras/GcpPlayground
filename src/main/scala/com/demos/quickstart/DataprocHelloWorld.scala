package com.demos.quickstart

import com.demos.utils.{ExecutionMode, StandardArgs}
import com.spark.utils.SparkSessionUtils.getSparkSession
import org.apache.log4j.Logger

object DataprocHelloWorld {

  private val logger: Logger = Logger.getLogger(getClass)

  class DataprocHelloWorldArgs(args: Seq[String]) extends StandardArgs(args) { verify() }

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new DataprocHelloWorldArgs(args)
    val executionMode = if (argsParsed.executionMode() == "local") ExecutionMode.local else ExecutionMode.GCP

    val spark = getSparkSession("Dataproc - Hello world", executionMode)

    val data = Seq(
      ("A", "Categoría1", 10, "..."),
      ("A", "Categoría2", 20, "---"),
      ("B", "Categoría1", 30, "´´´"),
      ("B", "Categoría2", 40, "^^^")
    )

    spark.createDataFrame(data).toDF("id", "cat", "valor", "not_important").show()

    spark.stop()

  }

}