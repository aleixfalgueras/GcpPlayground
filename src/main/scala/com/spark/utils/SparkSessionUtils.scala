package com.spark.utils

import com.demos.utils.ExecutionMode
import ExecutionMode.ExecutionMode
import org.apache.spark.sql.SparkSession

object SparkSessionUtils {

  def getSparkSession(appName: String,
                      executionMode: ExecutionMode = ExecutionMode.GCP,
                      timezone: String = "Europe/Sofia"): SparkSession = {
    val spark = if (executionMode == ExecutionMode.local) {
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

    // common config
    spark.conf.set("viewsEnabled","true")
    spark.conf.set("spark.sql.session.timeZone", timezone)

    spark

  }

}
