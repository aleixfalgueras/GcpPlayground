package com.demos.mis

import com.spark.utils.SparkSessionUtils.getSparkSession

/**
 * Simple example of how to connect Spark to a MySql instance using the JDBC.
 *
 */
object SparkMySql extends App {

  val MYSQL_USER = "pep_melos"
  val MYSQL_PASSWORD = "pepito"

  val spark = getSparkSession("Spark - MySql example")

  val jdbcUrl = "jdbc:mysql://34.72.246.168:3306/demo_db"
  val dbProperties = new java.util.Properties()

  dbProperties.setProperty("user", MYSQL_USER)
  dbProperties.setProperty("password", MYSQL_PASSWORD)
  dbProperties.setProperty("useSSL", "false")

  spark.read
    .jdbc(jdbcUrl, "players", dbProperties)
    .show()

}
