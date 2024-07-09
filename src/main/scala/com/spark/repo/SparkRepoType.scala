package com.spark.repo

import com.utils.EnumUtils.matchEnum

object SparkRepoType extends Enumeration {

  val BQ, CSV, AVRO, PARQUET = Value

  def apply(sparkRepoType: String): SparkRepoType.Value =  matchEnum(sparkRepoType, SparkRepoType)

}
