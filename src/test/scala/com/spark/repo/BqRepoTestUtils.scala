package com.spark.repo

import org.apache.spark.sql.DataFrame

object BqRepoTestUtils {

  def getPartitionIdAndTotalRows(partitionsInfo: DataFrame): Set[(String, Int)] = {
    partitionsInfo
      .select("partition_id", "total_rows")
      .collect()
      .map(row => (row.getAs[String](0), row.getAs[Int](1)))
      .toSet

  }

}
