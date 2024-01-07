import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger


object Main {

  private val logger: Logger = Logger.getLogger(Main.getClass)

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.getOrCreate()
    spark.sparkContext.setLogLevel("ERROR") // only affects spark framework messages

    val data = Seq(
      ("A", "Categoría1", 10, "..."),
      ("A", "Categoría2", 20, "---"),
      ("B", "Categoría1", 30, "´´´"),
      ("B", "Categoría2", 40, "^^^")
    )

    spark.createDataFrame(data).toDF("id", "cat", "valor", "not_important").show()

    logger.info("Hello world!")
  }

}