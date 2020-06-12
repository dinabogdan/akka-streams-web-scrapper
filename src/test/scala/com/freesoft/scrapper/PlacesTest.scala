package com.freesoft.scrapper

import akka.stream.scaladsl.Source
import org.scalatest.freespec.AnyFreeSpec

class PlacesTest extends AnyFreeSpec with AkkaSpec {

  "count" - {
    "should return 24 if the file provided is places.txt" in {
      val placesFilter = new PlacesFilter("places.txt")

      val count = placesFilter.count().futureValue

      assert(count === 24)
    }
    "should throw exception if the file provided not exists" in {
      val exception = intercept[IllegalArgumentException] {
        val placesFilter = new PlacesFilter("p.txt")
        placesFilter.count().futureValue
      }

      assert(exception.isInstanceOf[IllegalArgumentException])
    }
  }

}
