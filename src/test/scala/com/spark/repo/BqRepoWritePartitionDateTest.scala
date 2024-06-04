package com.spark.repo

import com.bq.BqClient
import com.demos.utils.DateTimeUtils
import com.google.cloud.spark.bigquery.repackaged.com.google.cloud.bigquery.TimePartitioning
import com.spark.repo.BqRepoWritePartitionDateTest._
import com.spark.repo.implementation.BqRepo
import org.apache.log4j.Logger
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SaveMode}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import utils.SparkTestUtils.getDf
import utils.{SparkTest, TestingConfig}

class BqRepoWritePartitionDateTest extends SparkTest {

  private val logger: Logger = Logger.getLogger(getClass)

  def getPartitionIdAndTotalRows(partitionsInfo: DataFrame): Set[(String, Int)] = {
    partitionsInfo
      .select("partition_id", "total_rows")
      .collect()
      .map(row => (row.getAs[String](0), row.getAs[Int](1)))
      .toSet

  }

  val studentsDailyRepo = new BqRepo(studentsDailyTableName, TestingConfig.gcsTmpBucket)
  val studentsMonthlyRepo = new BqRepo(studentsMonthlyTableName, TestingConfig.gcsTmpBucket)
  val studentsYearlyRepo = new BqRepo(studentsYearlyTableName, TestingConfig.gcsTmpBucket)
  val studentsHourlyRepo = new BqRepo(studentsHourlyTableName, TestingConfig.gcsTmpBucket)

  // ######## DATA ########

  // date

  val oneStudentDatePartition: DataFrame = getDf(Seq(
    Row("Pep", "Melós", 20, DateTimeUtils.getDateSql("01-01-2021"))
  ), studentsDateSchema)

  val multipleStudentsDatePartition: DataFrame= getDf(Seq(
    Row("Maria", "Filomena", 50, DateTimeUtils.getDateSql("02-02-2022")),
    Row("Pepito", "Meloso", 80, DateTimeUtils.getDateSql("03-03-2023"))
  ), studentsDateSchema)

  // timestamp

  val oneStudentTimestampPartition: DataFrame = getDf(Seq(
    Row("Pep", "Melós", 20, DateTimeUtils.getTimestampSqlFromString("01-01-2021 01:01:01"))
  ), studentsTimestampSchema)

  val multipleStudentsTimestampPartition: DataFrame = getDf(Seq(
    Row("Maria", "Filomena", 50, DateTimeUtils.getTimestampSqlFromString("02-02-2022 02:02:02")),
    Row("Pepito", "Meloso", 80, DateTimeUtils.getTimestampSqlFromString("03-03-2023 03:03:03"))
  ), studentsTimestampSchema)

  // ######## TESTS ########

  override def beforeAll(): Unit = {
    BqClient.createOrOverwritePartitionedTable(
      studentsDailyTableName,
      studentsDateSchema,
      studentsDatePartitionField
    )
    BqClient.createOrOverwritePartitionedTable(
      studentsMonthlyTableName,
      studentsDateSchema,
      studentsDatePartitionField,
      TimePartitioning.Type.MONTH
    )
    BqClient.createOrOverwritePartitionedTable(
      studentsYearlyTableName,
      studentsDateSchema,
      studentsDatePartitionField,
      TimePartitioning.Type.YEAR
    )
    BqClient.createOrOverwritePartitionedTable(
      studentsHourlyTableName,
      studentsTimestampSchema,
      studentsTimestampPartitionField,
      TimePartitioning.Type.HOUR
    )
    super.beforeAll()
  }

  behavior of "Function writePartitionDate(...) with different arguments"

  it must "behavior as expected for a daily partitioned table" in {
    val oneStudentDailyDatePartition = Some("20210101")

    studentsDailyRepo.writePartitionDate(
      multipleStudentsDatePartition,
      SaveMode.Overwrite,
      studentsDatePartitionField
    )

    studentsDailyRepo.writePartitionDate(
      oneStudentDatePartition,
      SaveMode.Overwrite,
      studentsDatePartitionField,
      datePartition = oneStudentDailyDatePartition
    )
    studentsDailyRepo.writePartitionDate(
      oneStudentDatePartition,
      SaveMode.Append,
      studentsDatePartitionField,
      datePartition = oneStudentDailyDatePartition
    )

    studentsDailyRepo.readPartitionsInfo().show(truncate = false)

    val expectedData = oneStudentDatePartition.union(oneStudentDatePartition).union(multipleStudentsDatePartition)

    assertDataFrameDataEquals(expectedData, studentsDailyRepo.read())
  }

  it must "behavior as expected for a monthly partitioned table" in {
    val oneStudentMonthlyDatePartition = Some("202101")

    studentsMonthlyRepo.writePartitionDate(
      multipleStudentsDatePartition,
      SaveMode.Overwrite,
      studentsDatePartitionField,
      partitionType = PartitionType.MONTH,
    )
    studentsMonthlyRepo.writePartitionDate(
      oneStudentDatePartition,
      SaveMode.Overwrite,
      studentsDatePartitionField,
      partitionType = PartitionType.MONTH,
      datePartition = oneStudentMonthlyDatePartition
    )

    val partitionIdAndRows = getPartitionIdAndTotalRows(studentsMonthlyRepo.readPartitionsInfo())
    val expectedPartitionIdAndRows = Set(("202101", 1), ("202303", 1), ("202202",1))

    expectedPartitionIdAndRows shouldEqual partitionIdAndRows
  }

  it must "behavior as expected for a yearly partitioned table" in {
    val oneStudentYearDatePartition = Some("2021")

    studentsYearlyRepo.writePartitionDate(
      multipleStudentsDatePartition,
      SaveMode.Overwrite,
      studentsDatePartitionField,
      partitionType = PartitionType.YEAR,
    )
    studentsYearlyRepo.writePartitionDate(
      oneStudentDatePartition,
      SaveMode.Overwrite,
      studentsDatePartitionField,
      partitionType = PartitionType.YEAR,
      datePartition = oneStudentYearDatePartition
    )
    studentsYearlyRepo.writePartitionDate(
      oneStudentDatePartition,
      SaveMode.Append,
      studentsDatePartitionField,
      partitionType = PartitionType.YEAR,
      datePartition = oneStudentYearDatePartition
    )

    val partitionIdAndRows = getPartitionIdAndTotalRows(studentsYearlyRepo.readPartitionsInfo())
    val expectedPartitionIdAndRows = Set(("2021", 2), ("2023", 1), ("2022", 1))

    expectedPartitionIdAndRows shouldEqual partitionIdAndRows
  }

  it must "behavior as expected for a hourly partitioned table" in {
    // ! make sure JVM timezone is UTC to achieve expected hour partition IDs (VM options: -Duser.timezone=UTC)
    val oneStudentHourDatePartition = Some("2021010101")

    studentsHourlyRepo.writePartitionDate(
      multipleStudentsTimestampPartition,
      SaveMode.Overwrite,
      studentsTimestampPartitionField,
      partitionType = PartitionType.HOUR,
    )

    studentsHourlyRepo.writePartitionDate(
      oneStudentTimestampPartition,
      SaveMode.Overwrite,
      studentsTimestampPartitionField,
      partitionType = PartitionType.HOUR,
      datePartition = oneStudentHourDatePartition
    )
    studentsHourlyRepo.writePartitionDate(
      oneStudentTimestampPartition,
      SaveMode.Append,
      studentsTimestampPartitionField,
      partitionType = PartitionType.HOUR,
      datePartition = oneStudentHourDatePartition
    )

    val partitionIdAndRows = getPartitionIdAndTotalRows(studentsHourlyRepo.readPartitionsInfo())
    val expectedPartitionIdAndRows = Set(("2021010101", 2), ("2023030303", 1), ("2022020202", 1))

    expectedPartitionIdAndRows shouldEqual partitionIdAndRows
  }

  override def afterAll(): Unit = {
    BqClient.deleteTable(studentsDailyTableName)
    BqClient.deleteTable(studentsMonthlyTableName)
    BqClient.deleteTable(studentsYearlyTableName)
    BqClient.deleteTable(studentsHourlyTableName)
    super.afterAll()
  }

}

object BqRepoWritePartitionDateTest {

  val studentsDateSchema: StructType = StructType(Seq(
    StructField("name", StringType, nullable = true),
    StructField("surname", StringType, nullable = true),
    StructField("age", IntegerType, nullable = true),
    StructField("birth_date", DateType, nullable = true)
  ))
  val studentsDatePartitionField = "birth_date"

  val studentsTimestampSchema: StructType = StructType(Seq(
    StructField("name", StringType, nullable = true),
    StructField("surname", StringType, nullable = true),
    StructField("age", IntegerType, nullable = true),
    StructField("birth_timestamp", TimestampType, nullable = true)
  ))
  val studentsTimestampPartitionField = "birth_timestamp"

  val studentsDailyTableName = s"${TestingConfig.projectId}.${TestingConfig.dataset}.students_daily"
  val studentsMonthlyTableName = s"${TestingConfig.projectId}.${TestingConfig.dataset}.students_monthly"
  val studentsYearlyTableName = s"${TestingConfig.projectId}.${TestingConfig.dataset}.students_yearly"
  val studentsHourlyTableName = s"${TestingConfig.projectId}.${TestingConfig.dataset}.students_hourly"

}
