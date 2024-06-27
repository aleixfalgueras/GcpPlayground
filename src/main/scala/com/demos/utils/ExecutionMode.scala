package com.demos.utils

import com.demos.utils.EnumUtils.matchEnum

object ExecutionMode extends Enumeration {

  val LOCAL, GCP = Value

  def apply(executionMode: String): ExecutionMode.Value = matchEnum(executionMode, ExecutionMode)

}