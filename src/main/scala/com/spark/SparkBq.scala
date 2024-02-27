package com.spark
import com.bq.BqType
import org.apache.log4j.Logger
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}


object SparkBq {
  private val logger: Logger = Logger.getLogger(getClass)

  /**
   * BQ type "BIGNUMERIC" (aka "BIGDECIMAL") precision is 76.76 (the 77th digit is partial), however Spark DecimalType
   * maximum precision allowed is 38.
   *
   * https://cloud.google.com/bigquery/docs/reference/standard-sql/data-types#numeric_types
   * https://spark.apache.org/docs/latest/sql-ref-datatypes.html
   */
  def getSparkType(bqType: BqType.BqType): DataType = {
    bqType match {
      case BqType.STRING => StringType
      case BqType.INT64 | BqType.INT | BqType.SMALLINT | BqType.INTEGER |
           BqType.BIGINT | BqType.TINYINT | BqType.BYTEINT => IntegerType
      case BqType.NUMERIC | BqType.DECIMAL => DecimalType(38, 9)
      case BqType.BIGNUMERIC | BqType.BIGDECIMAL => DecimalType(38, 38)
      case BqType.FLOAT64 => DoubleType
      case _ => throw new Exception(s"BigQuery type not known") // TODO: print enum name
    }

  }

  def readFromBq(spark: SparkSession, tableName: String): DataFrame = {
    logger.info(s"Reading $tableName from BigQuery...")
    spark.read.format("bigquery").load(tableName)

  }

  def writeInBq[T](ds: Dataset[T], tableName: String, mode: SaveMode, tmpBucket: String): Unit = {
    logger.info(s"Writing $tableName in BigQuery...")
    ds.write
      .format("bigquery")
      .mode(mode)
      .option("temporaryGcsBucket", tmpBucket)
      .save(tableName)

  }

}
