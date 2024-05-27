package com.demos.sparkexercises.etl

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec

trait SparkTest extends AnyFlatSpec with DataFrameSuiteBase {

  override implicit def reuseContextIfPossible: Boolean = true

  override implicit lazy val spark: SparkSession = SparkSession.builder()
    .appName("Spark tests")
    .master("local[*]")
    .getOrCreate()

}
