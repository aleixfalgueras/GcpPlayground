package com.spark.utils

import com.spark.demos.utils.ExecutionMode
import com.spark.demos.utils.ExecutionMode.ExecutionMode
import org.apache.spark.sql.SparkSession

object SparkSessionUtils {

  def getSparkSession(appName: String,
                      executionMode: ExecutionMode = ExecutionMode.GCP,
                      timezone: String = "Europe/Sofia"): SparkSession = {
    if (executionMode == ExecutionMode.local) {
      SparkSession.builder
        .master("local[*]")
        .appName(appName)
        .config("spark.sql.session.timeZone", timezone)
        .getOrCreate()
    }
    else {
      SparkSession.builder
        .appName(appName)
        .config("spark.sql.session.timeZone", timezone)
        .getOrCreate()
    }

  }

}
