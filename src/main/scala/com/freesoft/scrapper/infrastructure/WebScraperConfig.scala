package com.freesoft.scrapper.infrastructure

import com.typesafe.config.{Config, ConfigFactory}

class WebScraperConfig(val configFileName: String) {

  private lazy val config: Config = ConfigFactory.load(configFileName)

  lazy val baseUrl: String = config.getString("scraper.baseUrl")
  lazy val httpQueueBufferSize: Int = config.getInt("scraper.httpQueueBufferSize")

}
