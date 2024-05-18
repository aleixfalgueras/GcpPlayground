package com.spark.demos.utils

import org.rogach.scallop.{ScallopConf, ScallopOption}

class StandardArgs(args: Seq[String]) extends ScallopConf(args) {

  val env: ScallopOption[String] = choice(
    name = "env",
    choices = Seq("dev"),
    required = true,
    descr = "Environment: dev"
  )

  val executionMode: ScallopOption[String] = choice(
    name = "executionMode",
    choices = Seq("local", "GCP"),
    required = true,
    descr = "Execution mode: local, GCP"
  )

}
