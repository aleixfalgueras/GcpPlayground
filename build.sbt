
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18" // Spark 3 is pre-built with Scala 2.12

val sparkVersion = "3.5.0" // local version: Spark 3.5.0 built for Hadoop 3.3.4
val sparkTestingVersion = "3.5.0_1.5.3"

val scioVersion = "0.14.4"
val beamVersion = "2.55.1" // scio 0.14.x	- Apache Beam 2.x.x
val jacksonVersion = "2.15.2"

lazy val root = (project in file("."))
  .settings(
    name := "GcpPlayground",
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.rogach" %% "scallop" % "5.0.1",
      "com.github.pureconfig" %% "pureconfig" % "0.17.5",
      "org.mockito" %% "mockito-scala-scalatest" % "1.16.15" % Test,
      // spark
      "org.apache.spark" %% "spark-avro" % sparkVersion,
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      "com.google.cloud.bigdataoss" % "gcs-connector" % "hadoop3-2.2.19" % "provided",
      "com.holdenkarau" %% "spark-testing-base" % sparkTestingVersion % Test,
      // scio
      "org.slf4j" % "slf4j-simple" % "1.7.32",
      "com.spotify" %% "scio-core" % scioVersion,
      "com.spotify" %% "scio-google-cloud-platform" % scioVersion,
      "com.spotify" %% "scio-test" % scioVersion % Test,
      "org.apache.beam" % "beam-runners-direct-java" % beamVersion,
      "org.apache.beam" % "beam-runners-google-cloud-dataflow-java" % beamVersion,
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
      // required for executing in local with "fake" spark for windows
      "com.google.cloud.spark" %% "spark-bigquery-with-dependencies" % "0.35.1" % "provided",
      "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"
    ),
    dependencyOverrides ++= Seq(
      "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
    )
  )

// scala macro options
scalacOptions += "-Xplugin-require:macroparadise"
scalacOptions += "-Xmacro-settings:cache-implicit-schemas=true"

// holdenkarau recommended config
Test / fork := true
Test / parallelExecution := false
Test / javaOptions ++= Seq("-Xms8G", "-Xmx8G")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}
