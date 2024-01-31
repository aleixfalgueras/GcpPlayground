
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18" // Spark 3 is pre-built with Scala 2.12

val sparkVersion = "3.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "GcpPlayground",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion
    )
  )

assembly / assemblyShadeRules := Seq(
  ShadeRule.rename("org.apache.spark.**" -> "relocated.org.apache.spark.@1").inAll
)

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}