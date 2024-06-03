package com.demos.utils

import org.apache.log4j.Logger

import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate, LocalDateTime, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import scala.util.Try

object DateTimeUtils {

  private val logger: Logger = Logger.getLogger(getClass)

  val SIMPLE_DATE_FORMAT = "dd-MM-yyyy"
  val SIMPLE_TIMESTAMP_FORMAT = "dd-MM-yyyy HH:mm:ss"

  // ############ java.time ############

  def currentDate          : LocalDate     = LocalDate.now()     // date without timezone
  def currentDateTime      : LocalDateTime = LocalDateTime.now() // date-time without timezone
  def currentZonedDateTime : ZonedDateTime = ZonedDateTime.now() // date-time with timezone
  def currentTimestampZoned: Instant       = Instant.now()       // timestamp with timezone (nanosecond precision)

  /* Examples:
  currentDate           -> 2024-06-02
  currentDateTime       -> 2024-06-02T11:53:18.560716800
  currentZonedDateTime  -> 2024-06-02T11:53:18.560716800+02:00[Europe/Madrid]
  currentTimestampZoned -> 2024-06-02T09:53:18.560716800Z
  */

  def getDate(stringDate: String, format: String = SIMPLE_DATE_FORMAT): LocalDate =
    LocalDate.parse(stringDate, DateTimeFormatter.ofPattern(format))

  def toDateMultipleFormats(stringDate: String, formats: Seq[String]): Option[LocalDate] = {
    formats.map { format => Try(getDate(stringDate, format)) }.find(_.isSuccess) match {
      case Some(dateSuccess) => Some(dateSuccess.get)
      case None =>
        logger.error(s"Couldn't cast $stringDate to LocalDate using any provided format: $formats")
        None
    }

  }

  def zoneTimestamp(stringTimestamp: String, formatWithoutTimezone: String = SIMPLE_TIMESTAMP_FORMAT): Instant = {
    val localDateTime = LocalDateTime.parse(stringTimestamp, DateTimeFormatter.ofPattern(formatWithoutTimezone))
    val zonedLocalDateTime = localDateTime.atZone(ZoneId.systemDefault())
    zonedLocalDateTime.toInstant

  }

  // ############ java.sql ############ (use is discouraged unless necessary for Spark programs)

  def currentDateSql     : Date = Date.valueOf(currentDate)
  def currentTimestampSql: Timestamp = Timestamp.from(currentTimestampZoned) // ! timestamp without timezone (microsecond precision)

  def getDateSql(localDate: LocalDate): Date = Date.valueOf(localDate)

  def getDateSql(stringDate: String, format: String = SIMPLE_DATE_FORMAT): Date = Date.valueOf(
    getDate(stringDate, format)
  )

  def getTimestampSqlFromString(timestampString: String, format: String = SIMPLE_TIMESTAMP_FORMAT): Timestamp = {
    Timestamp.valueOf(LocalDateTime.parse(timestampString, DateTimeFormatter.ofPattern(format)))
  }


}
