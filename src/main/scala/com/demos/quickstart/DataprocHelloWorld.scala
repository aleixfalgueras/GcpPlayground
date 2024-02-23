package com.demos.quickstart

import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession


object DataprocHelloWorld {

  private val logger: Logger = Logger.getLogger(getClass)
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local[*]")
      .appName("Hello world")
      .getOrCreate()

    logger.info("Hello world")

    val data = Seq(
      ("A", "Categoría1", 10, "..."),
      ("A", "Categoría2", 20, "---"),
      ("B", "Categoría1", 30, "´´´"),
      ("B", "Categoría2", 40, "^^^")
    )

    spark.createDataFrame(data).toDF("id", "cat", "valor", "not_important").show()

  }

}