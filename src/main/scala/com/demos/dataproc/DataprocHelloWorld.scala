package com.demos.dataproc

import com.demos.utils.{ExecutionMode, StandardArgs}
import com.spark.utils.SparkSessionUtils.getSparkSession
import org.apache.log4j.Logger

/**
 * Simple example of how to run a Scala Spark application in Dataproc.
 * This code just creates and prints a dataframe.
 *
 */
object DataprocHelloWorld {

  private val logger: Logger = Logger.getLogger(getClass)

  class DataprocHelloWorldArgs(args: Seq[String]) extends StandardArgs(args) { verify() }

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new DataprocHelloWorldArgs(args)
    val executionMode = ExecutionMode(argsParsed.executionMode())

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