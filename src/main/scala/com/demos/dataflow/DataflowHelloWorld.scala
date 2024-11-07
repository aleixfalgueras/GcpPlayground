package com.demos.dataflow

import com.spotify.scio.ContextAndArgs
import com.spotify.scio.bigquery.types.BigQueryType
import com.spotify.scio.bigquery.{Query, Table, WRITE_TRUNCATE, bigQueryScioContextOps, description}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Simple example of how to use Dataflow with SCIO. Read data from [[BQ_SELLERS_TABLE]], apply map + partition function
 * and write into [[BQ_BAD_SELLERS_TABLE]].
 *
 */
object DataflowHelloWorld {

  private val logger: Logger = LoggerFactory getLogger getClass.getName

  val BQ_SELLERS_TABLE = "spark_exercises.sellers"
  @BigQueryType.fromTable("spark_exercises.sellers")
  class Seller

  val BQ_BAD_SELLERS_TABLE = "dataflow_exercises.bad_sellers"
  @BigQueryType.toTable
  @description("Sellers considered 'bad' seller")
  case class BadSeller(sellerName: String)

  def main(args: Array[String]): Unit = {
    logger.info("Args: " + args.mkString(", "))
    val (scioContext, parsedArgs) = ContextAndArgs(args)

    val (goodSellersEither, badSellersEither) = scioContext.typedBigQuery[Seller](Query(s"select * from $BQ_SELLERS_TABLE"))
      .map { seller => getGoodAndBadSellers(seller) }
      .partition {
        case Right(_) => true
        case Left(_) => false
      }

    val goodSellers = goodSellersEither.collect { case Right(goodSeller) => goodSeller }
    val badSellers = badSellersEither.collect { case Left(badSeller) => badSeller }

    goodSellers.map(goodSeller => logger.info(goodSeller.toString))
    badSellers.map(badSeller => logger.warn(badSeller.toString))

    badSellers.saveAsTypedBigQueryTable(Table.Spec(BQ_BAD_SELLERS_TABLE), writeDisposition = WRITE_TRUNCATE)

    scioContext.run

  }

  def getGoodAndBadSellers(seller: Seller): Either[BadSeller, Seller] = {
    if (seller.daily_target.getOrElse(0L) < 500000) Left(BadSeller(s"${seller.seller_name.orNull} is a bad seller"))
    else Right(Seller(seller.seller_id, Some(s"${seller.seller_name.orNull} is a good seller"), seller.daily_target))

  }

}
