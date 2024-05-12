package com.demos.sparkexercices

import com.demos.utils.StandardArgs
import com.spark.sparkrepo.SparkRepoType
import org.rogach.scallop.ScallopOption

class SparkExercicesEtlArgs(args: Seq[String]) extends StandardArgs(args) {

  val etl: ScallopOption[String] = choice(
    name = "etl",
    choices = Seq("products", "sellers", "sales", "all"),
    required = true,
    descr = "ETL to execute"
  )

  val targetRepo: ScallopOption[String] = choice(
    name = "targetRepo",
    choices = SparkRepoType.values.toSeq.map(_.toString),
    default = Some(SparkRepoType.bq.toString),
    required = true,
    descr = "Target repo type"
  )

  verify()

}
