package com.spark.repo

import scala.util.{Failure, Success, Try}

object SparkRepoType extends Enumeration {

  val BQ, CSV, AVRO, PARQUET = Value

  def apply(sparkRepoType: String): SparkRepoType.Value = {
    Try(withName(sparkRepoType.toUpperCase())) match {
      case Failure(_) => throw new Exception(s"SparkRepoType for $sparkRepoType not known")
      case Success(sparkRepoTypeValue) => sparkRepoTypeValue
    }
  }

}
