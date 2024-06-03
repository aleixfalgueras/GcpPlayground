package com.spark.repo

import scala.util.{Failure, Success, Try}

object PartitionType extends Enumeration {

  val HOUR, DAY, MONTH, YEAR = Value

  def apply(partitionType: String): PartitionType.Value = {
    Try(withName(partitionType.toUpperCase())) match {
      case Failure(_) => throw new Exception(s"PartitionType for $partitionType not known")
      case Success(sparkRepoTypeValue) => sparkRepoTypeValue
    }
  }

}
