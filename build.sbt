
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val sparkVersion = "3.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "GcpPlayground",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion
    )
  )

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", xs*) => MergeStrategy.concat
  case PathList("META-INF", xs*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}