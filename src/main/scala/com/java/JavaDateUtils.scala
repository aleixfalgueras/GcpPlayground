package com.java

import org.apache.log4j.Logger

import java.time.format.DateTimeFormatter
import scala.util.Try

case object JavaDateUtils {

  private val logger: Logger = Logger.getLogger(getClass)

  /*
  * Converts String dateString to a Some[LocalDate] if there's any format in dateFormats that matches dateString.
  * Returns None in any other case.
  */
  def toLocalDateMultipleFormats(dateString: String, dateFormats: Seq[String]): Option[java.time.LocalDate] = {
    dateFormats.map { stringDateFormat =>
      Try(java.time.LocalDate.parse(dateString, DateTimeFormatter.ofPattern(stringDateFormat)))
    }
      .find(_.isSuccess) match {
      case Some(dateSuccess) => Some(dateSuccess.get)
      case None =>
        logger.error(s"Couldn't cast $dateString to LocalDate using any provided format: $dateFormats")
        None
    }

  }

}
