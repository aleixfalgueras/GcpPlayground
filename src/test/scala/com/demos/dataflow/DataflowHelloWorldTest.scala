package com.demos.dataflow

import com.spotify.scio.bigquery._
import com.spotify.scio.testing._

class DataflowHelloWorldTest extends PipelineSpec {

  "DataflowHelloWorld" should "classify sellers correctly" in {
    val sellers = Seq(
      DataflowHelloWorld.Seller(Some("1"), Some("Alice"), Some(100000)),
      DataflowHelloWorld.Seller(Some("2"), Some("Bob"), Some(600000)),
      DataflowHelloWorld.Seller(Some("3"), Some("Charlie"), None)
    )

    val expectedBadSellers = Seq(
      DataflowHelloWorld.BadSeller("Alice is a bad seller"),
      DataflowHelloWorld.BadSeller("Charlie is a bad seller")
    )

    JobTest[DataflowHelloWorld.type]
      .input(BigQueryIO[DataflowHelloWorld.Seller]("select * from spark_exercises.sellers"), sellers)
      .output(BigQueryIO[DataflowHelloWorld.BadSeller]("dataflow_exercises.bad_sellers")) { badSellers =>
        badSellers should containInAnyOrder(expectedBadSellers)
      }
      .run()

  }

}
