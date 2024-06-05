package com.demos.utils

import scala.util.{Failure, Success, Try}

object EnumUtils {

  def matchEnum[Enum <: Enumeration](stringEnum: String, enum: Enum): enum.Value =
    Try(enum.withName(stringEnum)) match {
      case Success(enumValue) => enumValue
      case Failure(_) => throw new Exception(
        s"Value '$stringEnum' not known for the enumeration $enum. $enum values: ${enum.values.toSeq.mkString(", ")}.")
    }

}
