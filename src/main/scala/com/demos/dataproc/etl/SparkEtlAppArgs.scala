package com.demos.dataproc.etl

import com.demos.utils.StandardArgs
import com.spark.repo.SparkRepoType
import org.rogach.scallop.ScallopOption

class SparkEtlAppArgs(args: Seq[String]) extends StandardArgs(args) {

  val etl: ScallopOption[String] = choice(
    name = "etl",
    choices = Seq("products", "sellers", "sales", "all"),
    required = true,
    descr = "ETL to execute"
  )

  val targetRepo: ScallopOption[String] = choice(
    name = "targetRepo",
    choices = SparkRepoType.values.toSeq.map(_.toString),
    default = Some(SparkRepoType.BQ.toString),
    required = true,
    descr = "Target repo type"
  )

  verify()

}
