package utils

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.SparkSession
import org.scalatest.flatspec.AnyFlatSpec

/**
 * SparkTest is a testing trait for setting up and managing a local Spark session for unit tests.
 * It extends `AnyFlatSpec` from ScalaTest and `DataFrameSuiteBase` from Spark Testing Base to provide
 * a standardized environment for writing Spark tests.
 *
 * This trait includes:
 * - A reusable Spark context, configured to run locally with multiple threads (`local[*]`),
 *   allowing parallel execution of Spark jobs within tests.
 * - Configuration options specifically for testing, such as enabling Spark SQL views.
 *
 * By mixing in `SparkTest`, tests gain access to the configured Spark session and additional
 * functionality provided by `DataFrameSuiteBase`, such as implicit conversions for easier
 * DataFrame comparisons.
 */
trait SparkTest extends AnyFlatSpec with DataFrameSuiteBase {

  override implicit def reuseContextIfPossible: Boolean = true

  override implicit lazy val spark: SparkSession = SparkSession.builder()
    .appName("Spark tests")
    .master("local[*]")
    .getOrCreate()

  spark.conf.set("viewsEnabled","true")

}
