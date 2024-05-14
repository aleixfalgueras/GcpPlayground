package com.spark.repo
import com.bq.BqType
import org.apache.log4j.Logger
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

class BqSparkRepo(tableName: String, gcsTmpBucket: String)(implicit spark: SparkSession) extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    logger.info(s"Reading $tableName from BigQuery...")
    spark.read.format("bigquery").load(tableName)

  }

  override def write[T](data: Dataset[T], mode: SaveMode): Unit = {
    logger.info(s"Writing $tableName in BigQuery...")
    data.write
      .format("bigquery")
      .mode(mode)
      .option("temporaryGcsBucket", gcsTmpBucket)
      .save(tableName)

  }

  /**
   * BQ type "BIGNUMERIC" (aka "BIGDECIMAL") precision is 76.76 (the 77th digit is partial), however Spark DecimalType
   * maximum precision allowed is 38.
   *
   * https://cloud.google.com/bigquery/docs/reference/standard-sql/data-types#numeric_types
   * https://spark.apache.org/docs/latest/sql-ref-datatypes.html
   */
  def getEquivalentSparkTypeForBqType(bqType: BqType.Value): DataType = {
    bqType match {
      case BqType.STRING => StringType
      case BqType.INT64 | BqType.INT | BqType.SMALLINT | BqType.INTEGER |
           BqType.BIGINT | BqType.TINYINT | BqType.BYTEINT => IntegerType
      case BqType.NUMERIC | BqType.DECIMAL => DecimalType(38, 9)
      case BqType.BIGNUMERIC | BqType.BIGDECIMAL => DecimalType(38, 38)
      case BqType.FLOAT64 => DoubleType
      case _ => throw new Exception(s"BigQuery type $bqType not known")
    }
  }

}
