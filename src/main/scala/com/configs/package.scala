package com

package object configs {

  case class GCP(projectId: String,
                 demosBucket: String,
                 bqTmpBucket: String)

  case class SparkExercisesConfig(gcp: GCP,
                                  timezone: String,
                                  productsTable: String,
                                  sellersTable: String,
                                  salesTable: String,
                                  productsSourceGcsPath: String,
                                  sellersSourceGcsPath: String,
                                  salesSourceGcsPath: String,
                                  productsTargetGcsPath: String,
                                  sellersTargetGcsPath: String,
                                  salesTargetGcsPath: String)

}
