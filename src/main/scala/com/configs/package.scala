package com

package object configs {

  case class GCP(projectId: String,
                 demosBucket: String)

  case class SparkExercicesEtlConfig(gcp: GCP,
                                     productsTable: String,
                                     sellersTable: String,
                                     salesTable: String,
                                     productsPath: String,
                                     sellersPath: String,
                                     salesPath: String)

}
