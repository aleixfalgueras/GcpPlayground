package com.demos.dataproc.etl

import com.demos.dataproc.etl.SparkEtl.getSellersDailyTargetByTeam
import SparkEtlTest.{sellersExpectedTargetPath, sellersSchemaPath, sellersSourcePath}
import com.spark.repo.implementation.CsvRepo
import com.spark.utils.BqSparkSchema.getSparkSchema
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import utils.SparkTest

class SparkEtlTest extends SparkTest {

  val sellersSourceSchema: StructType = getSparkSchema(sellersSchemaPath)
  val sellersSourceRepo = new CsvRepo(sellersSourcePath, Some(sellersSourceSchema))

  val sellersExpectedTargetSchema: StructType = StructType(Seq(
    StructField("team", IntegerType, nullable = true),
    StructField("daily_target_by_team", IntegerType, nullable = true)
  ))
  val sellersExpectedTargetRepo = new CsvRepo(sellersExpectedTargetPath, Some(sellersExpectedTargetSchema))

  "sellersDailyTargetByTeam" should "return the sellersExpectedTarget df" in {
    val sellersExpectedTarget = sellersExpectedTargetRepo.read()
    val sellersTarget = getSellersDailyTargetByTeam(sellersSourceRepo.read())

    assertDataFrameDataEquals(sellersExpectedTarget, sellersTarget)
  }

}

object SparkEtlTest {

  val sellersDir = "src/test/resources/demos/dataproc/etl/sellers"

  val sellersSourcePath = s"$sellersDir/source.csv"
  val sellersSchemaPath = s"$sellersDir/source_schema.json"

  val sellersExpectedTargetPath = s"$sellersDir/expected_target.csv"

}
