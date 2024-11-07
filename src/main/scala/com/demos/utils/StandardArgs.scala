package com.demos.utils

import org.rogach.scallop.{ScallopConf, ScallopOption}

/**
 * Shared arguments for demos package applications.
 *
 */
class StandardArgs(args: Seq[String]) extends ScallopConf(args) {

  val env: ScallopOption[String] = choice(
    name = "env",
    choices = Seq("DEV"),
    required = true,
    descr = "Environments: DEV"
  )

  val executionMode: ScallopOption[String] = choice(
    name = "executionMode",
    choices = ExecutionMode.values.toSeq.map(_.toString),
    required = true,
    descr = "Execution mode: local, GCP"
  )

}
