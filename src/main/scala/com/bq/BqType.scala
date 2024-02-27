package com.bq

object BqType extends Enumeration {
  type BqType = Value

  val STRING, INT64, INT, SMALLINT, INTEGER, BIGINT, TINYINT, BYTEINT,
  NUMERIC, DECIMAL, BIGNUMERIC, BIGDECIMAL, FLOAT64 = Value
}
