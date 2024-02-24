package com.demos.quickstart

import com.demos.utils.StandardCmdLineArgs
import com.spark.ExecutionMode
import com.spark.SparkSessionUtils.getSparkSession
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession


object DataprocHelloWorld {

  private val logger: Logger = Logger.getLogger(getClass)
  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val argsParsed = new StandardCmdLineArgs(args)
    val executionMode = if (argsParsed.executionMode() == "local") ExecutionMode.local else ExecutionMode.GCP

    val spark = getSparkSession("Dataproc - Hello world", executionMode)

    val data = Seq(
      ("A", "Categoría1", 10, "..."),
      ("A", "Categoría2", 20, "---"),
      ("B", "Categoría1", 30, "´´´"),
      ("B", "Categoría2", 40, "^^^")
    )

    spark.createDataFrame(data).toDF("id", "cat", "valor", "not_important").show()

  }

}