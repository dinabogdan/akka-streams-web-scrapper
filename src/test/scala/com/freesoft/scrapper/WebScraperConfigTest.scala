package com.freesoft.scrapper

import org.scalatest.freespec.AnyFreeSpec

class WebScraperConfigTest extends AnyFreeSpec with AkkaSpec {

  "web scraper config" - {
    "baseUrl return www.imobiliare.ro" in {
      assert("www.imobiliare.ro" == webScraperConfig.baseUrl)
    }
  }

}
