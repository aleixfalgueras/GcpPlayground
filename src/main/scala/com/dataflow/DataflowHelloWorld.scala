package com.dataflow

import com.spotify.scio.ContextAndArgs
import com.spotify.scio.bigquery.types.BigQueryType
import com.spotify.scio.bigquery.{Query, bigQueryScioContextOps}
import org.slf4j.{Logger, LoggerFactory}

object DataflowHelloWorld {

  private val logger: Logger = LoggerFactory getLogger getClass.getName

  val BQ_SELLERS_TABLE = "spark_exercises.sellers"
  @BigQueryType.fromTable("spark_exercises.sellers")
  class seller

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))

    val (scioContext, parsedArgs) = ContextAndArgs(args)

    val result = scioContext.typedBigQuery[seller](Query(s"select * from $BQ_SELLERS_TABLE"))

    result.map(row => logger.info(row.toString))

    scioContext.run

  }

}
