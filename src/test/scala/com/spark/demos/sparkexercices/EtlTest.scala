package com.spark.demos.sparkexercices

import com.spark.demos.sparkexercices.etl.Etl.getSellersDailyTargetByTeam
import EtlTest.{sellersExpectedTargetPath, sellersSchemaPath, sellersSourcePath}
import com.spark.SparkTest
import com.spark.utils.BqSparkSchema.getSparkSchema
import com.spark.repo.CsvRepo
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}

class EtlTest extends SparkTest {

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

object EtlTest {

  val sellersDir = "src/test/resources/sparkexercices/sellers"

  val sellersSourcePath = s"$sellersDir/source.csv"
  val sellersSchemaPath = s"$sellersDir/source_schema.json"

  val sellersExpectedTargetPath = s"$sellersDir/expected_target.csv"

}
