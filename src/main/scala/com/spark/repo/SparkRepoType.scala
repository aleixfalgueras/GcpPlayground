package com.spark.repo


object SparkRepoType extends Enumeration {

  val avro, parquet, bq = Value

  def getSparkRepoType(repoType: String): SparkRepoType.Value = repoType.toLowerCase() match {
    case "avro" => SparkRepoType.avro
    case "parquet" => SparkRepoType.parquet
    case "bq" => SparkRepoType.bq
    case _ => throw new Exception(s"SparkRepoType for $repoType not known")
  }

}
