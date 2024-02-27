package com.demos.sparkexercices

import com.demos.utils.StandardArgs
import org.rogach.scallop.ScallopOption

class SparkExercicesEtlArgs(args: Seq[String]) extends StandardArgs(args) {

  val etl: ScallopOption[String] = choice(
    name = "etl",
    choices = Seq("products", "sellers", "sales", "all"),
    required = true,
    descr = "ETL to execute: products, sellers, sales, all"
  )

  verify()

}
