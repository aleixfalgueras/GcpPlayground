package com.spark.downloader

import com.spark.repo.SparkRepoType
import org.rogach.scallop.{ScallopConf, ScallopOption}

class DownloaderArgs(args: Seq[String]) extends ScallopConf(args) {

  val source: ScallopOption[String] = opt[String](
    name = "source",
    required = true,
    descr = "Source reference: a BQ table name or a GCS path"
  )

  val sourceRepo: ScallopOption[String] = choice(
    name = "sourceRepo",
    choices = SparkRepoType.values.toSeq.map(_.toString),
    required = true,
    descr = "Source repo"
  )

  val targetPath: ScallopOption[String] = opt[String](
    name = "targetPath",
    default = Some(s"src/main/resources/downloader/tmp/"),
    required = false,
    descr = "Destination path of the downloaded file"
  )

  val targetRepo: ScallopOption[String] = choice(
    name = "targetRepo",
    choices = Seq(SparkRepoType.csv.toString, SparkRepoType.avro.toString, SparkRepoType.parquet.toString),
    required = true,
    descr = "Format of the downloaded file"
  )

  val whereFilter: ScallopOption[String] = opt[String](
    name = "whereFilter",
    default = None,
    required = false,
    descr = "Spark where filter to apply to the source data"
  )

  verify()
}
