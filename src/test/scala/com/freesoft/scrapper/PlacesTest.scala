package com.freesoft.scrapper

import com.freesoft.scrapper.infrastructure.{FileContentSourceProvider, PlacesFilter}
import org.scalatest.freespec.AnyFreeSpec

class PlacesTest extends AnyFreeSpec with AkkaSpec {

  "count" - {
    "should return 24 if the file provided is places.txt" in {
      val placesFilter = new FileContentSourceProvider[PlacesFilter]("places.txt", { s => PlacesFilter(s) })

      val count = placesFilter.count().futureValue

      assert(count === 24)
    }
    "should throw exception if the file provided not exists" in {
      val exception = intercept[IllegalArgumentException] {
        val placesFilter = new FileContentSourceProvider[PlacesFilter]("p.txt", { s => PlacesFilter(s) })
        placesFilter.count().futureValue
      }

      assert(exception.isInstanceOf[IllegalArgumentException])
    }
  }
}
