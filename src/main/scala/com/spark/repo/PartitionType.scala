package com.spark.repo

import com.demos.utils.EnumUtils.matchEnum

object PartitionType extends Enumeration {

  val HOUR, DAY, MONTH, YEAR = Value

  def apply(partitionType: String): PartitionType.Value = matchEnum(partitionType, PartitionType)

}