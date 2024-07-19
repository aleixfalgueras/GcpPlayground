package com.demos.dataflow.taxiworkshop

import com.spotify.scio.bigquery.types.BigQueryType
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser.decode
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{Instant, Interval}

import scala.util.{Failure, Success, Try}

object TaxiDataTypes {
  protected val dateFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSZ")

  def json2TaxiRide(jsonStr: String): Either[Error, PointTaxiRide] = decode[PointTaxiRide](jsonStr)

  def circeErrorToCustomError(origError: Error): JsonError = JsonError(origError.getMessage)

  protected lazy implicit val instantDecoder: Decoder[Instant] =
    Decoder.decodeString.emap { s: String =>
      val ti: Try[Instant] = Try(dateFormatter.parseDateTime(s).toInstant)

      ti match {
        case Success(instant: Instant) => Right(instant)
        case Failure(e: Throwable) => Left(e.getMessage)
      }
    }

  protected lazy implicit val taxiRideDecoder: Decoder[PointTaxiRide] = deriveDecoder[PointTaxiRide]

  @BigQueryType.toTable
  case class PointTaxiRide(
                            ride_id: String,
                            point_idx: Int,
                            latitude: Double,
                            longitude: Double,
                            timestamp: Instant,
                            meter_reading: Double,
                            meter_increment: Double,
                            ride_status: String,
                            passenger_count: Int
                          ) {
    def toTaxiRide: TaxiRide =
      TaxiRide(this.ride_id,
        1,
        this.timestamp,
        None,
        this.meter_increment,
        this.ride_status,
        None
      )
  }

  @BigQueryType.toTable
  case class TaxiRide(
                       ride_id: String,
                       n_points: Int,
                       init: Instant,
                       finish: Option[Instant],
                       total_meter: Double,
                       init_status: String,
                       finish_status: Option[String]
                     ) {
    def +(taxiRide: TaxiRide): TaxiRide = {
      val (first, second) =
        if (this.init.isAfter(taxiRide.init)) {
          (taxiRide, this)
        } else {
          (this, taxiRide)
        }

      val (finishStatus: Option[String], finishInstant: Option[Instant]) = first.finish match {
        case None =>
          (Some(second.finish_status.getOrElse(second.init_status)),
            Some(second.finish.getOrElse(second.init)))
        case Some(i) =>
          val interval: Interval = new Interval(first.init, i)
          val testInstant: Instant = second.finish.getOrElse(second.init)
          if (interval.contains(testInstant)) {
            (Some(first.finish_status.getOrElse(first.init_status)),
              Some(first.finish.getOrElse(first.init)))
          } else {
            (Some(second.finish_status.getOrElse(second.init_status)),
              Some(second.finish.getOrElse(second.init)))
          }
      }

      TaxiRide(
        taxiRide.ride_id,
        first.n_points + 1,
        first.init,
        finishInstant,
        first.total_meter + second.total_meter,
        first.init_status,
        finishStatus
      )
    }
  }

  @BigQueryType.toTable
  case class JsonError(msg: String)

}
