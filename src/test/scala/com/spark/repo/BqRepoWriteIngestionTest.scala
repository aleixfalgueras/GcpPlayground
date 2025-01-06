package com.spark.repo

import com.bq.BqClient
import com.google.cloud.spark.bigquery.repackaged.com.google.cloud.bigquery.TimePartitioning
import com.spark.repo.BqRepoTestUtils.getPartitionIdAndTotalRows
import com.spark.repo.BqRepoWriteIngestionTest._
import com.spark.repo.implementation.BqRepo
import com.utils.DateTimeUtils._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import utils.SparkTestUtils.getDf
import utils.{SparkTest, TestingConfig}

class BqRepoWriteIngestionTest extends SparkTest {

  // ######## DATA ########

  val oneStudentPartition: DataFrame = getDf(Seq(
    Row("Pep", "Melós", 20)
  ), studentsSchema)

  // ######## TESTS ########

  val studentsIngestionTimeDailyBqRepo = new BqRepo(studentsIngestionTimeDailyTableName,  TestingConfig.gcsTmpBucket)
  val studentsIngestionTimeHourlyBqRepo = new BqRepo(studentsIngestionTimeHourlyTableName,  TestingConfig.gcsTmpBucket)

  override def beforeAll(): Unit = {
    if (TestingConfig.createTables) {
      BqClient.createOrOverwritePartitionedTable(
        studentsIngestionTimeDailyTableName,
        studentsSchema
      )
      BqClient.createOrOverwritePartitionedTable(
        studentsIngestionTimeHourlyTableName,
        studentsSchema,
        partitionType = TimePartitioning.Type.HOUR
      )
    }
    else {
      studentsIngestionTimeDailyBqRepo.truncateRepo()
      studentsIngestionTimeHourlyBqRepo.truncateRepo()
    }
    super.beforeAll()

  }

  behavior of "A daily partitioned table by ingestion time"

  it must "have daily partition after the write" in {
    studentsIngestionTimeDailyBqRepo.writeInIngestionPartitionedTable(oneStudentPartition, SaveMode.Overwrite)

    val partitionIdAndRows = getPartitionIdAndTotalRows(studentsIngestionTimeDailyBqRepo.readPartitionsInfo())
    val expectedPartitionIdAndRows = Set((formatDate(currentDate, BQ_DATE_PARTITION_FORMAT), 1))

    expectedPartitionIdAndRows shouldEqual partitionIdAndRows
  }

  it must "return the partitions from the interval [currentDate - 1 d, currentDate + 1 d] using " +
    " readByPartitionTimeInterval and the current date partition using readByPartitionDate after the write" in {
    studentsIngestionTimeDailyBqRepo.writeInIngestionPartitionedTable(oneStudentPartition, SaveMode.Overwrite)

    val currentDateMinus1Day = currentDate.minusDays(1).atStartOfDay()
    val currentDatePlus1Day = currentDate.plusDays(1).atStartOfDay()

    val dataUsingPartitionDate = studentsIngestionTimeDailyBqRepo.readByPartitionDate(currentDate)
    val dataUsingInterval = studentsIngestionTimeDailyBqRepo.readBy_partitiontimeInterval(
      currentDateMinus1Day,
      currentDatePlus1Day
    )

    val expectedData = oneStudentPartition

    assertDataFrameDataEquals(expectedData, dataUsingInterval.drop("PARTITIONTIME"))
    assertDataFrameDataEquals(expectedData, dataUsingPartitionDate.drop("PARTITIONTIME"))
  }

  behavior of "An hourly partitioned table by ingestion time"

  it must "have an hourly partition after the write" in {
    studentsIngestionTimeHourlyBqRepo.writeInIngestionPartitionedTable(oneStudentPartition, SaveMode.Overwrite)

    val partitionIdAndRows = getPartitionIdAndTotalRows(studentsIngestionTimeHourlyBqRepo.readPartitionsInfo())
    val expectedPartitionIdAndRows = Set((formatDateTime(currentDateTime, BQ_HOUR_PARTITION_FORMAT), 1))

    studentsIngestionTimeHourlyBqRepo.readPartitionsInfo().show(false)

    expectedPartitionIdAndRows shouldEqual partitionIdAndRows
  }

  it must "return the partitions from the interval [currentDateTime - 1 H, currentDateTime + 1 H] using " +
    " readByPartitionTimeInterval and the current hour partition using readByPartitionTime after the write" in {
    studentsIngestionTimeHourlyBqRepo.writeInIngestionPartitionedTable(oneStudentPartition, SaveMode.Overwrite)

    val currentDateTimeMinus1H = currentDateTime.minusHours(1)
    val currentDateTimePlus1H = currentDateTime.plusHours(1)

    val dataUsingPartitionDate = studentsIngestionTimeHourlyBqRepo.readBy_partitiontime(currentDateTime)
    val dataUsingInterval = studentsIngestionTimeHourlyBqRepo.readBy_partitiontimeInterval(
      currentDateTimeMinus1H,
      currentDateTimePlus1H
    )

    dataUsingPartitionDate.show()
    dataUsingInterval.show()

    val expectedData = oneStudentPartition

    assertDataFrameDataEquals(expectedData, dataUsingInterval.drop("PARTITIONTIME"))
    assertDataFrameDataEquals(expectedData, dataUsingPartitionDate.drop("PARTITIONTIME"))
  }

  override def afterAll(): Unit = {
    if (TestingConfig.createTables) {
      BqClient.deleteTable(studentsIngestionTimeDailyTableName)
      BqClient.deleteTable(studentsIngestionTimeHourlyTableName)
    }
    super.afterAll()

  }

}

object BqRepoWriteIngestionTest {

  val studentsSchema: StructType = StructType(Seq(
    StructField("name", StringType, nullable = true),
    StructField("surname", StringType, nullable = true),
    StructField("age", IntegerType, nullable = true)
  ))

  val studentsIngestionTimeDailyTableName =
    s"${TestingConfig.projectId}.${TestingConfig.dataset}.students_ingestion_time_daily"

  val studentsIngestionTimeHourlyTableName =
    s"${TestingConfig.projectId}.${TestingConfig.dataset}.students_ingestion_time_hourly"

}
