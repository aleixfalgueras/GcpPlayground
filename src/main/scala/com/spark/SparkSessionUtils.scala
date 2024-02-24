package com.spark

import org.apache.spark.sql.SparkSession

object SparkSessionUtils {

  def getSparkSession(appName: String, executionMode: ExecutionMode.Value = ExecutionMode.GCP): SparkSession = {
    if (executionMode == ExecutionMode.local) {
      SparkSession.builder
        .master("local[*]")
        .appName(appName)
        .getOrCreate()
    }
    else {
      SparkSession.builder
        .appName(appName)
        .getOrCreate()
    }

  }

}
