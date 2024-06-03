package utils

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object SparkTestUtils {

  def getDf(data: Seq[Row], schema: StructType)(implicit spark: SparkSession): DataFrame =
    spark.createDataFrame(spark.sparkContext.parallelize(data), schema)

}
