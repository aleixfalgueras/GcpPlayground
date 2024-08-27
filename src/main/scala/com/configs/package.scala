package com

package object configs {

  // shared GCP config values
  case class GCP(projectId: String,
                 demosBucket: String,
                 bqTmpBucket: String)

  case class SparkEtlConfig(gcp: GCP,
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
