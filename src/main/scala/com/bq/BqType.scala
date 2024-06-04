package com.bq

import scala.util.{Failure, Success, Try}

object BqType extends Enumeration {

  val BOOLEAN, STRING, INT64, INT, SMALLINT, INTEGER, BIGINT, TINYINT, BYTEINT,
  NUMERIC, DECIMAL, BIGNUMERIC, BIGDECIMAL, FLOAT64, DATE, DATETIME, TIMESTAMP = Value

  def apply(bqType: String): BqType.Value = {
    Try(withName(bqType)) match {
      case Failure(_) => throw new Exception(s"BigQuery type $bqType not known")
      case Success(bqTypeValue) => bqTypeValue
    }
  }

}
