package com.spark.demos.sparkexercices.etl

import com.spark.demos.sparkexercices.domain.Product
import com.spark.repo.SparkRepo
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DecimalType, IntegerType}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object Etl {

  def getSellersDailyTargetByTeam(sellersData: DataFrame): DataFrame = {
    sellersData
      .withColumn("daily_target", col("daily_target").cast(IntegerType))
      .withColumn("team", when(col("seller_id").cast(IntegerType) < lit(5), 0).otherwise(1))
      .groupBy("team")
      .agg(sum("daily_target").as("daily_target_by_team"))
      .select("team", "daily_target_by_team")

  }

  def etlSellers(sourceRepo: SparkRepo, targetRepo: SparkRepo)(implicit spark: SparkSession): Unit = {
    val transformedSellers = sourceRepo.read()
      .withColumn("daily_target", col("daily_target").cast(IntegerType))

    targetRepo.write(transformedSellers, SaveMode.Overwrite)

  }

  def etlProducts(sourceRepo: SparkRepo, targetRepo: SparkRepo)(implicit spark: SparkSession): Unit = {
    import spark.implicits._
    val productDs = sourceRepo.read()
      .withColumn("price", col("price").cast(DecimalType(38, 9)))
      .as[Product]

    targetRepo.write(productDs, SaveMode.Overwrite)

  }

  def etlSales(sourceRepo: SparkRepo, targetRepo: SparkRepo)(implicit spark: SparkSession): Unit = {
    val transformedSales = sourceRepo.read()
      .withColumn("num_pieces_sold", col("num_pieces_sold").cast(IntegerType))
      .withColumn("date", to_date(col("date"), "yyyy-MM-dd"))

    targetRepo.write(transformedSales, SaveMode.Overwrite)

  }

}
