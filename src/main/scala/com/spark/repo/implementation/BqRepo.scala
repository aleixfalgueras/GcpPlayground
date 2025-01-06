package com.spark.repo.implementation

import com.bq.BqClient
import com.spark.repo.implementation.BqRepo.ONLY_READ_REPO
import com.spark.repo.{PartitionType, SparkRepo}
import com.utils.DateTimeUtils.formatDateTimeISO
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

import java.time.{LocalDate, LocalDateTime}
import scala.util.{Failure, Success, Try}

class BqRepo(val tableName: String, val gcsTmpBucket: String = ONLY_READ_REPO)(implicit spark: SparkSession)
  extends SparkRepo {

  private val logger: Logger = Logger.getLogger(getClass)

  override def read(): DataFrame = {
    if (BqClient.isExternalTable(tableName)) readExternalTable()
    else {
      logger.info(s"Reading $tableName from BigQuery...")
      spark.read.format("bigquery").load(tableName)
    }

  }

  override def write[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    logger.info(s"Writing BigQuery table $tableName...")
    data.write
      .format("bigquery")
      .mode(saveMode)
      .option("writeMethod", "direct")
      .save(tableName)

  }

  /** At today, Direct write method does not support writing into a partitioned ingestion table. */
  def writeInIngestionPartitionedTable[T](data: Dataset[T], saveMode: SaveMode): Unit = {
    data.write
      .format("bigquery")
      .mode(saveMode)
      .option("temporaryGcsBucket", gcsTmpBucket)
      .save(tableName)

  }

  def readExternalTable(): DataFrame = {
    spark.conf.set("materializationDataset", tableName.split('.')(1))
    val query = s"SELECT * FROM `$tableName`"

    Try {
      logger.info(s"Reading from external table $tableName with query $query...")
      spark.read
        .format("bigquery")
        .options(Map("viewsEnabled" -> "true", "query" -> query))
        .load()
    } match {
      case Failure(exc) => logger.error(s"Error trying to read external table $tableName"); throw exc;
      case Success(df) => df
    }

  }

  def readPartitionsInfo(): DataFrame = {
    val tableNameSplit = tableName.split('.')
    val query =
      s"""
      SELECT partition_id, total_rows, total_logical_bytes, last_modified_time
      FROM `${tableNameSplit(0)}.${tableNameSplit(1)}.INFORMATION_SCHEMA.PARTITIONS`
      WHERE table_name = '${tableNameSplit(2)}'
      ORDER BY last_modified_time DESC
      """

    logger.info(s"Reading partitions info of BigQuery table $tableName")
    runSqlQuery(query)

  }

  /**
   * Reads the partitions in the interval [startDateTime, endDateTime] using the columnn _PARTITIONTIME.
   * The value of _PARTITIONTIME is based on the UTC date when the field is populated.
   *
   * To improve query performance, use the _PARTITIONTIME pseudo-column by itself on the left side of a comparison.
   *
   * @param startDateTime starting date time point
   * @param endDateTime ending date time point
   */
  def readBy_partitiontimeInterval(startDateTime: LocalDateTime, endDateTime: LocalDateTime): DataFrame = {
    val startTimestampFormatted = formatDateTimeISO(startDateTime)
    val endTimestampFormatted = formatDateTimeISO(endDateTime)
    val query =
      s"""
      |SELECT
      |  *, _PARTITIONTIME AS PARTITIONTIME
      |FROM
      |  $tableName
      |WHERE
      |  _PARTITIONTIME BETWEEN TIMESTAMP('$startTimestampFormatted') AND TIMESTAMP('$endTimestampFormatted')
      |""".stripMargin

    runSqlQuery(query)

  }

  /**
   * Reads the partition for the partitionTime provided, seconds and minutes from partitionTime are ignored
   * because BQ's minimum partition level is hourly.
   *
   */
  def readBy_partitiontime(partitionTime: LocalDateTime): DataFrame = {
    val partitionTimeHour = partitionTime.withMinute(0).withSecond(0).withNano(0)
    val partitionTimeFormatted = formatDateTimeISO(partitionTimeHour)
    val query =
      s"""
         |SELECT
         |  *, _PARTITIONTIME AS PARTITIONTIME
         |FROM
         |  $tableName
         |WHERE
         | _PARTITIONTIME = TIMESTAMP('$partitionTimeFormatted')
         |""".stripMargin

    runSqlQuery(query)

  }

  def readByPartitionDate(partitionDate: LocalDate): DataFrame =
    readBy_partitiontime(partitionDate.atStartOfDay())

  /**
   *
   * Writes data to a BigQuery table partitioned by HOUR, DAY, MONTH, or YEAR.
   *
   * For daily partitioning, only options "partitionField" or "datePartition" are required (because of default values):
   *  partitionField + Overwrite -> Overwrite the entire table with the new partitions.
   *  partitionField + Append    -> Append records to the existing partitions and create new partitions if necessary.
   *  datePartition  + Overwrite -> Overwrite only the specified partition.
   *  datePartition  + Append    -> Append new records only the specified partition.
   *
   * For other partitioning strategies:
   *  insert multiple partitions -> partitionField + partitionType required
   *  insert single partition    -> datePartition + partitionField + partitionType required
   *
   * Ref: https://github.com/GoogleCloudDataproc/spark-bigquery-connector
   *
   * @param partitionField partitioning field of the table
   * @param partitionType partition type of the table, possible values: HOUR, DAY, MONTH, YEAR
   * @param datePartition date of the specific partition to write, valid formats: yyyyMMddHH, yyyyMMdd, yyyyMM, yyyy
   */
  def writePartitionDate[T](data: Dataset[T],
                            saveMode: SaveMode,
                            partitionField: String,
                            partitionType: PartitionType.Value = PartitionType.DAY,
                            datePartition: Option[String] = None): Unit = {
    val options = datePartition match {
      case Some(datePartitionValue) =>
        logger.info(s"Writing partition in BigQuery table $tableName: \n" +
          s"[partition ID = $datePartitionValue, partition field = $partitionField, " +
          s"partition type = $partitionType, mode = $saveMode]")
        Map(
          "temporaryGcsBucket" -> gcsTmpBucket,
          "datePartition" -> datePartitionValue,
          "partitionField" -> partitionField,
          "partitionType" -> partitionType.toString
        )
      case None =>
        logger.info(s"Writing partitions in BigQuery table $tableName: \n" +
          s"[partition ID = multiple inserts, partition field = $partitionField, " +
          s"partition type = $partitionType, mode = $saveMode]")
        Map(
          "temporaryGcsBucket" -> gcsTmpBucket,
          "partitionField" -> partitionField,
          "partitionType" -> partitionType.toString
        )
    }

    data.write
      .format("bigquery")
      .mode(saveMode)
      .options(options)
      .save(tableName)

  }

  def truncateRepo(): Unit = BqClient.truncateTable(tableName)

  def runSqlQuery(query: String): DataFrame = {
    spark.conf.set("viewsEnabled", "true")
    spark.conf.set("materializationDataset", tableName.split('.')(1))

    logger.info(s"Running query: \n$query")
    spark.read
      .format("bigquery")
      .option("query", query)
      .load()

  }

}

object BqRepo {

  val ONLY_READ_REPO = "ONLY_READ_REPO"

}
