package com.freesoft.scrapper

import akka.stream.scaladsl.Sink
import com.freesoft.scrapper.infrastructure.ImobiliareHttpRequestProvider
import org.scalatest.freespec.AnyFreeSpec

class ImobiliareHttpRequestProviderTest extends AnyFreeSpec with AkkaSpec {

  "imobiliare http request provider" - {
    "should return HttpRequestURIs for multiple sale types" in {
      val imobiliareHttpRequestProvider = new ImobiliareHttpRequestProvider(
        saleTypeFilterProvider,
        immobileTypesFilterProvider,
        placesFilterProvider
      )

      val result = imobiliareHttpRequestProvider.addSaleTypes
        .runWith(pageNumbers, Sink.seq)
        ._2
        .futureValue
        .flatten

      assert(result.nonEmpty)
      assert(result.size == 40)
    }
  }
}
