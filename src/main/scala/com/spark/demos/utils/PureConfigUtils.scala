package com.spark.demos.utils

import org.apache.log4j.Logger
import pureconfig.{ConfigReader, ConfigSource}


object PureConfigUtils {
  private val logger: Logger = Logger.getLogger(getClass.getName)

  def readConfigFromFile[T](env: String, path: String)(implicit reader: ConfigReader[T]): T = {
    logger.info(s"Reading config in $path (env=$env) using $reader...")

    ConfigSource
      .resources(path)
      .at(env)
      .load[T] match {
      case Right(conf) => conf
      case Left(err) =>
        err.toList.foreach(e => logger.error(s"$e"))
        throw new Exception(s"Wrong config in $path (env=$env)")
    }

  }

}
