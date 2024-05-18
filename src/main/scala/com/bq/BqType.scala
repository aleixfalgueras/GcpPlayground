package com.bq

import scala.util.{Failure, Success, Try}

object BqType extends Enumeration {
  type BqType = Value

  val BOOLEAN, STRING, INT64, INT, SMALLINT, INTEGER, BIGINT, TINYINT, BYTEINT,
  NUMERIC, DECIMAL, BIGNUMERIC, BIGDECIMAL, FLOAT64, DATE, TIMESTAMP = Value

  def getBqType(bqType: String): BqType.Value = {
    Try(withName(bqType)) match {
      case Failure(exception) => throw new Exception(s"BigQuery type $bqType not known")
      case Success(bqTypeValue) => bqTypeValue
    }
  }

}
