package com.demos.dataproc.sparkexercises

import com.configs.SparkExercisesConfig
import com.demos.utils.PureConfigUtils.readConfigFromFile
import com.spark.repo.implementation.ParquetRepo
import com.spark.utils.SparkSessionUtils.getSparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import pureconfig.generic.auto._

import scala.collection.immutable.Seq

object SqlExercises {

  val PRODUCT_ID = "product_id"
  val PIECES_SOLD = "num_pieces_sold"

  // ! adding type annotation crash the code
  implicit val sparkExercisesEtlConfigReader = pureconfig.ConfigReader[SparkExercisesConfig]
  val configFilePath = "config/spark_exercises.conf"

  /* WARM-UP 1: Which is the product contained in more orders? */
  def warmUp1(products: DataFrame, sales: DataFrame, sellers: DataFrame): Unit = {
    println(s"Products: ${products.count()}, " + s"sales ${sales.count()} and sellers ${sellers.count()}")

    println(s"At least have been sold ${sales.select("product_id").distinct().count()} products")

    val top_product = sales
      .groupBy(PRODUCT_ID)
      .agg(count("product_id").as("product_id_sales"))
      .orderBy(col("product_id_sales").desc)
      .first()(0)

    println("top product id: " + top_product)

  }

  /* WARM-UP 2: How many distinct products have been sold in each day? */
  def warmUp2(products: DataFrame, sales: DataFrame, sellers: DataFrame): Unit = {
    sales
      .groupBy("date")
      .agg(countDistinct(PRODUCT_ID) as "Distinct products sold")
      .show()
  }

  /* Ex 1: What is the average revenue of the orders? */
  def ex1(products: DataFrame, sales: DataFrame, sellers: DataFrame): Unit = {
    sales
      .join(products, Seq("product_id"), "inner")
      .withColumn("revenue", col("price") * col(PIECES_SOLD))
      .select(avg("revenue"))
      .show()
  }

  /* Ex 2: For each seller, what is the average % contribution of an order to the seller's daily quota? */
  def ex2(products: DataFrame, sales: DataFrame, sellers: DataFrame): Unit = {
    sales
      .join(broadcast(sellers), Seq("seller_id"), "inner")
      .withColumn("contribution", col(PIECES_SOLD) / col("daily_target"))
      .groupBy("seller_id")
      .agg(avg("contribution").as("avg_contribuition"))
      .show()
  }

  /* Ex 3
   Who are the second most selling and the least selling persons (sellers) for each product?
   Who are those for product with `product_id = 0` */
  def ex3(sales: DataFrame): Unit = {
    val product_window = Window.partitionBy("product_id").orderBy(col("total_PS").asc)

    sales
      .withColumn(PIECES_SOLD, col(PIECES_SOLD).cast(IntegerType))
      .withColumn(PRODUCT_ID, col(PRODUCT_ID).cast(IntegerType))
      .groupBy("product_id", "seller_id")
      .agg(sum(PIECES_SOLD) as "total_PS")
      .withColumn("top_sellers", row_number().over(product_window))
      .withColumn("next_seller", lead("seller_id", 1).over(product_window))
      .where("top_sellers = 2 or next_seller is null")
      .show()

  }

  /* Ex 4
  Create a new column called "hashed_bill" defined as follows:
      - if the order_id is even: apply MD5 hashing iteratively to the bill_raw_text field,
      once for each 'A' (capital 'A') present in the text. E.g. if the bill text is 'nbAAnllA',
      you would apply hashing three times iteratively (only if the order number is even)
      - if the order_id is odd: apply SHA256 hashing to the bill text

  Finally, check if there are any duplicate on the new column*/
  def ex4(spark: SparkSession): Unit = {
    def someFancyOperation = (colValueOne: Int, colValue2: Int) => {
      // some operation not possible with sql.functions
      colValueOne * colValue2
    }

    // to use it in a withColumn()
    val someFancyOperationUDF = udf(someFancyOperation)

    // to use it spark.sql
    spark.udf.register("someFancyOperationUDF", someFancyOperation)

  }

  def joinExamples(products: DataFrame, sales: DataFrame, sellers: DataFrame): Unit = {
    // left-semi -> like inner but only keep the columns form the left df
    sales.join(sellers, Seq("seller_id"), "leftsemi").show()

    // left-anti -> !left-semi -> get the registers from the left df non-matching the right df
    sales.join(sellers, Seq("seller_id"), "leftanti").show()

  }

  def explode_example(spark: SparkSession): Unit = {
    val schema = StructType(Seq(
      StructField("name", StringType, nullable = true),
      StructField("properties", MapType(StringType, IntegerType)),
      StructField("languages", ArrayType(StringType))))

    val data = Seq(
      Row("aleix", Map("key0" -> 10, "key00" -> 30), Seq("C", "Sharp")),
      Row("marc", Map("key1" -> 1, "key11" -> 3), Seq("java", "Python")))

    val df = spark.createDataFrame(spark.sparkContext.parallelize(data), schema)

    df.show()
    // use explode_outer to keep nuls
    // use posexplode to know the position nuls
    df.select(col("name"), explode(col("properties"))).show()
    df.withColumn("map_keys", explode(map_keys(col("properties")))).show()
    df.withColumn("map_values", explode(map_values(col("properties")))).show()
    df.withColumn("explode_array", explode(col("languages")).as("arry_exploded")).show()

  }

  def pivot_example(spark: SparkSession): Unit = {
    val data = Seq(
      ("A", "Categoría1", 10, "..."),
      ("A", "Categoría2", 20, "---"),
      ("B", "Categoría1", 30, "´´´"),
      ("B", "Categoría2", 40, "^^^")
    )

    val df = spark.createDataFrame(data).toDF("id", "cat", "valor", "not_important")

    val pivotedDF = df
      .groupBy("id")
      .pivot("cat")
      .sum("valor")
    pivotedDF.show()
  }

  def main(args: Array[String]): Unit = {
    implicit val spark: SparkSession = getSparkSession("SqlExercises")

    implicit val config = readConfigFromFile("dev", configFilePath)

    val products = new ParquetRepo(config.productsSourceGcsPath).read()
    val sales = new ParquetRepo(config.salesSourceGcsPath).read()
    val sellers = new ParquetRepo(config.sellersSourceGcsPath).read()

    products.cache(); sales.cache(); sellers.cache()

    warmUp1(products, sales, sellers)
    warmUp2(products, sales, sellers)

    ex1(products, sales, sellers)
    ex2(products, sales, sellers)
    ex3(sales)
    ex4(spark)
    joinExamples(products, sales, sellers)
    explode_example(spark)
    pivot_example(spark)

  }

}