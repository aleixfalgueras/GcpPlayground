package com.spark.repo


object SparkRepoType extends Enumeration {

  val bq, csv, avro, parquet = Value

  def getSparkRepoType(repoType: String): SparkRepoType.Value = repoType.toLowerCase() match {
    case "bq" => SparkRepoType.bq
    case "csv" => SparkRepoType.csv
    case "avro" => SparkRepoType.avro
    case "parquet" => SparkRepoType.parquet
    case _ => throw new Exception(s"SparkRepoType for $repoType not known")
  }

}
