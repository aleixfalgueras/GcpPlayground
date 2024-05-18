package com.spark

import com.bq.BqType
import com.bq.BqType.getBqType
import org.apache.spark.sql.types._
import org.json4s._
import org.json4s.jackson.JsonMethods.parse

import scala.io.Source

/* import org.json4s._ // sometimes, this import is removed automatically and not auto-detected later */
object BqSparkSchema {

  /**
   * BQ type "BIGNUMERIC" (aka "BIGDECIMAL") precision is 76.76 (the 77th digit is partial), however Spark DecimalType
   * maximum precision allowed is 38.
   *
   * https://cloud.google.com/bigquery/docs/reference/standard-sql/data-types#numeric_types
   * https://spark.apache.org/docs/latest/sql-ref-datatypes.html
   */
  def bqTypeToSparkType(bqType: BqType.Value): DataType = {
    bqType match {
      case BqType.BOOLEAN => BooleanType
      case BqType.STRING => StringType
      case BqType.INT64 | BqType.INT | BqType.SMALLINT | BqType.INTEGER |
           BqType.BIGINT | BqType.TINYINT | BqType.BYTEINT => IntegerType
      case BqType.NUMERIC | BqType.DECIMAL => DecimalType(38, 9)
      case BqType.BIGNUMERIC | BqType.BIGDECIMAL => DecimalType(38, 38)
      case BqType.FLOAT64 => DoubleType
      case BqType.DATE => DateType
      case BqType.TIMESTAMP => TimestampType
      case _ => throw new Exception(s"Spark type for BigQuery type $bqType not known")
    }
  }

  def bqTypeToSparkType(bqType: String): DataType = bqTypeToSparkType(getBqType(bqType))

  def getSparkSchema(schemaPath: String): StructType = {
    val source = Source.fromFile(schemaPath)
    val stringJson = source.mkString
    val json = parse(stringJson)
    source.close()

    val fields = for {
      JObject(child) <- json
      JField("name", JString(name)) <- child
      JField("type", JString(bqType)) <- child
      JField("mode", JString(mode)) <- child
    } yield StructField(name, bqTypeToSparkType(bqType), mode != "REQUIRED")

    StructType(fields)

  }


}