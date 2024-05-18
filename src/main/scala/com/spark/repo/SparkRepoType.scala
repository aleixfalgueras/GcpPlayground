package com.spark.repo

import scala.util.{Failure, Success, Try}

object SparkRepoType extends Enumeration {

  val bq, csv, avro, parquet = Value

  def getSparkRepoType(sparkRepoType: String): SparkRepoType.Value = {
    Try(withName(sparkRepoType)) match {
      case Failure(exception) => throw new Exception(s"SparkRepoType for $sparkRepoType not known")
      case Success(sparkRepoTypeValue) => sparkRepoTypeValue
    }
  }

}
