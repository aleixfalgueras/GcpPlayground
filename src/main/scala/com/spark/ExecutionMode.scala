package com.spark

object ExecutionMode extends Enumeration {
  type ExecutionMode = Value
  val local, GCP = Value
}
