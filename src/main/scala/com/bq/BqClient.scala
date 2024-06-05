
package com.bq

import com.google.cloud.spark.bigquery.repackaged.com.google.cloud.bigquery._
import com.spark.utils.BqSparkSchema
import org.apache.spark.sql.types.StructType
import org.slf4j.{Logger, LoggerFactory}

object BqClient {

  val logger: Logger = LoggerFactory getLogger getClass.getName

  val bigQuery: BigQuery = BigQueryOptions.getDefaultInstance.getService

  // ############ DATASET ############

  def createDataset(projectId: String, datasetName: String): Unit = {
    val datasetId = DatasetId.of(projectId, datasetName)
    val datasetInfo = DatasetInfo.newBuilder(datasetId).build()

    logger.info(s"Creating dataset $projectId.$datasetName")
    bigQuery.create(datasetInfo)
  }

  def deleteDataset(projectId: String, datasetName: String, deleteContents: Boolean = true): Boolean = {
    val datasetId = DatasetId.of(projectId, datasetName)

    val result = if (deleteContents) {
      bigQuery.delete(datasetId, BigQuery.DatasetDeleteOption.deleteContents())
    } else {
      bigQuery.delete(datasetId)
    }

    if (result) logger.info(s"Dataset $datasetName deleted successfully")
    else logger.info(s"Dataset $datasetName was not found")

    result

  }

  // ############ TABLE ############

  def getTableIdByTableName(tableName: String): TableId = {
    val tableNameSplit = tableName.split('.')
    TableId.of(tableNameSplit(0), tableNameSplit(1), tableNameSplit(2))
  }

  def createTable(tableName: String, sparkSchema: StructType): Unit = {
    val bqSchema = BqSparkSchema.getBqSchema(sparkSchema)
    val tableId = getTableIdByTableName(tableName)
    val tableDefinition = StandardTableDefinition.of(bqSchema)
    val tableInfo = TableInfo.of(tableId, tableDefinition)

    logger.info(s"Creating table $tableName")
    bigQuery.create(tableInfo)
  }

  def createOrOverwriteTable(tableName: String, sparkSchema: StructType): Unit = {
    if (bigQuery.getTable(getTableIdByTableName(tableName)) != null) deleteTable(tableName)
    createTable(tableName, sparkSchema)

  }

  def createPartitionedTable(tableName: String,
                             sparkSchema: StructType,
                             partitionField: String,
                             partitionType: TimePartitioning.Type = TimePartitioning.Type.DAY): Unit = {

    val bqSchema = BqSparkSchema.getBqSchema(sparkSchema)
    val tableId = getTableIdByTableName(tableName)
    val timePartitioning = TimePartitioning.newBuilder(partitionType)
      .setField(partitionField)
      .build()

    val tableDefinition = StandardTableDefinition.newBuilder()
      .setSchema(bqSchema)
      .setTimePartitioning(timePartitioning)
      .build()

    val tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build()

    logger.info(s"Creating partitioned table $tableName of type $partitionType by $partitionField")
    bigQuery.create(tableInfo)

  }

  def createOrOverwritePartitionedTable(tableName: String,
                                        sparkSchema: StructType,
                                        partitionField: String,
                                        partitionType: TimePartitioning.Type = TimePartitioning.Type.DAY): Unit = {
    if (bigQuery.getTable(getTableIdByTableName(tableName)) != null) deleteTable(tableName)
    createPartitionedTable(tableName, sparkSchema, partitionField, partitionType)

  }

  def deleteTable(tableName: String): Boolean = {
    val result = bigQuery.delete(getTableIdByTableName(tableName))

    if (result) logger.info(s"Table $tableName deleted successfully")
    else logger.info(s"Table $tableName was not found")

    result

  }

  def truncateTable(tableName: String): Unit = {
    logger.info(s"Truncating table $tableName")
    runQuery(s"TRUNCATE TABLE $tableName")

  }

  private def runQuery(query: String): Unit = {
    val queryConfig = QueryJobConfiguration.newBuilder(query).build()
    val job = bigQuery.create(JobInfo.of(queryConfig))
    job.waitFor()

    logger.info(s"Executing BigQuery query: \n$query")

    if (job.getStatus.getError != null) {
      throw new RuntimeException(s"BigQuery job failed: ${job.getStatus.getError.getMessage}")
    } else {
      println(s"Query executed successfully.")
    }

  }


}
