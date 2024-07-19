package com.demos.dataflow.taxiworkshop

import com.demos.dataflow.taxiworkshop.TaxiDataTypes._
import com.spotify.scio.bigquery.{CREATE_IF_NEEDED, Table, WRITE_APPEND, WRITE_TRUNCATE}
import com.spotify.scio.pubsub.PubsubIO
import com.spotify.scio.values.{SCollection, WindowOptions}
import com.spotify.scio.{Args, ContextAndArgs, ScioContext, streaming}
import org.apache.beam.sdk.transforms.windowing.{AfterProcessingTime, AfterWatermark}
import org.joda.time.Duration

object TaxiSessionsPipeline {
  val SESSION_GAP = 600
  val EARLY_RESULT = 10
  val LATENESS = 900

  def main(cmdlineArgs: Array[String]): Unit = {
    val (scontext: ScioContext, opts: Args) = ContextAndArgs(cmdlineArgs)
    implicit val sc: ScioContext = scontext

    val pubsubTopic = opts("pubsub-topic")
    val goodTable = opts("output-table")
    val badTable = opts("errors-table")
    val accumTable = opts("accum-table")

    val messages = getMessagesFromPubSub(pubsubTopic)
    val (rides, writableErrors) = parseJSONStrings(messages)

    rides.saveAsTypedBigQueryTable(
      Table.Spec(goodTable),
      writeDisposition = WRITE_APPEND,
      createDisposition = CREATE_IF_NEEDED
    )
    writableErrors.saveAsTypedBigQueryTable(
      Table.Spec(badTable),
      writeDisposition = WRITE_APPEND,
      createDisposition = CREATE_IF_NEEDED
    )

    val groupRides = groupRidesByKey(rides.map(_.toTaxiRide), customWindowOptions)

    groupRides.saveAsTypedBigQueryTable(
      Table.Spec(accumTable),
      writeDisposition = WRITE_APPEND,
      createDisposition = CREATE_IF_NEEDED
    )

    sc.run

  }

  def customWindowOptions: WindowOptions =
    WindowOptions(
      trigger = AfterWatermark.pastEndOfWindow()
        .withEarlyFirings(AfterProcessingTime
          .pastFirstElementInPane
          .plusDelayOf(Duration.standardSeconds(EARLY_RESULT)))
        .withLateFirings(AfterProcessingTime
          .pastFirstElementInPane()
          .plusDelayOf(Duration.standardSeconds(LATENESS))),
      accumulationMode = streaming.ACCUMULATING_FIRED_PANES,
      allowedLateness = Duration.standardSeconds(LATENESS)
    )

  def getMessagesFromPubSub(pubsubTopic: String)(implicit sc: ScioContext): SCollection[String] = {
    sc.read(PubsubIO.string(pubsubTopic, timestampAttribute = "ts"))(PubsubIO.ReadParam(PubsubIO.Topic))

  }

  def parseJSONStrings(messages: SCollection[String]): (SCollection[PointTaxiRide], SCollection[JsonError]) = {
    val (pointTaxiRides, errors) = messages.map(msg => json2TaxiRide(msg)).partition(_.isRight)
    val jsonErrors = errors.collect { case Left(error) => circeErrorToCustomError(error) }

    (pointTaxiRides.collect { case Right(pointTaxiRide) => pointTaxiRide }, jsonErrors)

  }

  def groupRidesByKey(rides: SCollection[TaxiRide], wopts: WindowOptions): SCollection[TaxiRide] = {
    rides
      .keyBy(_.ride_id)
      .withSessionWindows(Duration.standardSeconds(SESSION_GAP), wopts)
      .reduceByKey(_ + _)
      .map(_._2)

  }

}
