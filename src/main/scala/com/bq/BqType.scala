package com.bq

import com.demos.utils.EnumUtils.matchEnum

object BqType extends Enumeration {

  val BOOLEAN, STRING, INT64, INT, SMALLINT, INTEGER, BIGINT, TINYINT, BYTEINT,
  NUMERIC, DECIMAL, BIGNUMERIC, BIGDECIMAL, FLOAT64, DATE, DATETIME, TIMESTAMP = Value

  def apply(bqType: String): BqType.Value = matchEnum(bqType, BqType)

}
