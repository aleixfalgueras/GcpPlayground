package com.demos.sparkexercices

import com.bq.BqType
import com.configs.SparkExercicesEtlConfig
import com.demos.sparkexercices.domain.Product
import com.spark.SparkBq.{getSparkType, writeInBq}
import com.spark.SparkGCS.readFromGcs
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, to_date}

object SparkExercicesEtl {

  def etlSellers()(implicit spark: SparkSession, config: SparkExercicesEtlConfig): Unit = {
    val sellers = readFromGcs(spark, config.sellersPath)

    val transformedSellers = sellers
      .withColumn("daily_target", col("daily_target").cast(getSparkType(BqType.INT64)))

    writeInBq(transformedSellers, config.sellersTable, SaveMode.Overwrite, config.gcp.bqTmpBucket)

  }

  def etlProducts()(implicit spark: SparkSession, config: SparkExercicesEtlConfig): Unit = {
    val productsDf = readFromGcs(spark, config.productsPath)

    import spark.implicits._
    val productDs = productsDf
      .withColumn("price", col("price").cast(getSparkType(BqType.NUMERIC)))
      .as[Product]

    writeInBq(productDs, config.productsTable, SaveMode.Overwrite, config.gcp.bqTmpBucket)

  }

  def etlSales()(implicit spark: SparkSession, config: SparkExercicesEtlConfig): Unit = {
    val sales = readFromGcs(spark, config.salesPath)

    val transformedSales = sales
      .withColumn("num_pieces_sold", col("num_pieces_sold").cast(getSparkType(BqType.INT64)))
      .withColumn("date", to_date(col("date"), "yyyy-MM-dd"))

    writeInBq(transformedSales, config.salesTable, SaveMode.Overwrite, config.gcp.bqTmpBucket)

  }

}
