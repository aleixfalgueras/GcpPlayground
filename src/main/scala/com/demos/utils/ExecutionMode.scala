package com.demos.utils

import scala.util.{Failure, Try, Success}

object ExecutionMode extends Enumeration {

  val LOCAL, GCP = Value

  def apply(executionMode: String): ExecutionMode.Value = {
    Try(withName(executionMode.toUpperCase())) match {
      case Failure(_) => throw new Exception(s"ExecutionMode for $executionMode not known")
      case Success(executionModeValue) => executionModeValue
    }
  }

}
