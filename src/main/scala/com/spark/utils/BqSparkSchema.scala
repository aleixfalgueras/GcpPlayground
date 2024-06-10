package com.spark.utils

import com.bq.BqType
import com.google.cloud.spark.bigquery.repackaged.com.google.cloud.bigquery.{Field, Schema, StandardSQLTypeName}
import org.apache.spark.sql.types._
import org.json4s._
import org.json4s.jackson.JsonMethods.parse

import scala.io.Source

/* import org.json4s._  sometimes this import is removed automatically and not auto-detected later */
object BqSparkSchema {

  /**
   * BQ type "BIGNUMERIC" (aka "BIGDECIMAL") precision is 76.76 (the 77th digit is partial), however Spark DecimalType
   * maximum precision allowed is 38.
   *
   * https://github.com/GoogleCloudDataproc/spark-bigquery-connector?tab=readme-ov-file#data-types
   * https://cloud.google.com/bigquery/docs/reference/standard-sql/data-types#numeric_types
   * https://spark.apache.org/docs/latest/sql-ref-datatypes.html
   */
  def bqTypeToSparkType(bqType: BqType.Value): DataType = {
    bqType match {
      case BqType.BOOLEAN => BooleanType
      case BqType.STRING => StringType
      case BqType.INT64 | BqType.INT | BqType.SMALLINT | BqType.INTEGER | BqType.BIGINT | BqType.TINYINT |
           BqType.BYTEINT => IntegerType
      case BqType.NUMERIC | BqType.DECIMAL => DecimalType(38, 9)
      case BqType.BIGNUMERIC | BqType.BIGDECIMAL => DecimalType(38, 38)
      case BqType.FLOAT64 => DoubleType
      case BqType.DATE => DateType
      case BqType.DATETIME => TimestampNTZType
      case BqType.TIMESTAMP => TimestampType
      case _ => throw new Exception(s"Spark type for BigQuery type $bqType not known")
    }
  }

  def bqTypeToSparkType(bqType: String): DataType = bqTypeToSparkType(BqType(bqType))

  /**
   * Converts a BigQuery JSON schema definition into a Spark StructType schema.
   *
   * @param schemaPath path to the JSON file with the BigQuery schema
   * @return the corresponding Spark Schema
   */
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

  /**
   * Converts a Spark StructType schema to a BigQuery Schema.
   *
   * @param structType the Spark StructType schema
   * @return the corresponding BigQuery Schema
   */
  def getBqSchema(structType: StructType): Schema = {
    val fields = structType.fields.map { field =>
      val fieldType = field.dataType match {
        case IntegerType => StandardSQLTypeName.INT64
        case LongType => StandardSQLTypeName.INT64
        case FloatType => StandardSQLTypeName.FLOAT64
        case DoubleType => StandardSQLTypeName.FLOAT64
        case StringType => StandardSQLTypeName.STRING
        case BooleanType => StandardSQLTypeName.BOOL
        case TimestampType => StandardSQLTypeName.TIMESTAMP
        case DateType => StandardSQLTypeName.DATE
        case _ => throw new IllegalArgumentException(s"Unsupported Spark data type: ${field.dataType}")
      }
      Field.newBuilder(field.name, fieldType).setMode(Field.Mode.NULLABLE).build()
    }

    Schema.of(fields: _*)

  }


}