
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18" // Spark 3 is pre-built with Scala 2.12

val sparkVersion = "3.5.0" // local version: Spark 3.5.0 built for Hadoop 3.3.4


lazy val root = (project in file("."))
  .settings(
    name := "GcpPlayground",
    libraryDependencies ++= Seq(
      "org.rogach" %% "scallop" % "5.0.1",
      "com.github.pureconfig" %% "pureconfig" % "0.17.5",
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-avro" % sparkVersion % "provided",
      "com.google.cloud.bigdataoss" % "gcs-connector" % "hadoop3-2.2.19" % "provided",
      // required for executing in local with "fake" spark for windows
      "com.google.cloud.spark" %% "spark-bigquery-with-dependencies" % "0.35.1" % "provided",
      "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"
    )
  )

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}