package com.demos.sparkexercices

import com.SparkTest
import com.demos.sparkexercices.SparkExercicesEtl.getSellersDailyTargetByTeam
import com.demos.sparkexercices.SparkExercicesEtlTest.{sellersExpectedTargetPath, sellersSchemaPath, sellersSourcePath}
import com.spark.BqSparkSchema.getSparkSchema
import com.spark.repo.CsvRepo
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}

class SparkExercicesEtlTest extends SparkTest {

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

object SparkExercicesEtlTest {

  val sellersDir = "src/test/resources/sparkexercices/sellers"

  val sellersSourcePath = s"$sellersDir/source.csv"
  val sellersSchemaPath = s"$sellersDir/source_schema.json"

  val sellersExpectedTargetPath = s"$sellersDir/expected_target.csv"

}
