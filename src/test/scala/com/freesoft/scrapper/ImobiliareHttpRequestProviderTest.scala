package com.freesoft.scrapper

import akka.stream.scaladsl.Sink
import com.freesoft.scrapper.infrastructure.{HttpRequestURI, ImobiliareHttpRequestProvider}
import org.scalatest.freespec.AnyFreeSpec

class ImobiliareHttpRequestProviderTest extends AnyFreeSpec with AkkaSpec {

  "imobiliare http request provider" - {
    val imobiliareHttpRequestProvider = new ImobiliareHttpRequestProvider(
      saleTypeFilterProvider,
      immobileTypesFilterProvider,
      placesFilterProvider
    )
    "should return HttpRequestURIs for multiple sale types" in {

      val result = imobiliareHttpRequestProvider.addSaleTypes
        .runWith(pageNumbers, Sink.seq)
        ._2
        .futureValue
        .flatten

      assert(result.nonEmpty)
      assert(result.size == 40)
    }

    "should concatenate places to existing HttpRequestURIs that contains the sale types" in {
      val result = imobiliareHttpRequestProvider.addSaleTypes
        .via(imobiliareHttpRequestProvider.addPlaces)
        .runWith(pageNumbers, Sink.seq)
        ._2
        .futureValue
        .flatten

      assert(result.nonEmpty)
      assert(result.size == 24 * 40)

      assert(result.contains(HttpRequestURI(saleType = Some("vanzare-apartamente"), place = Some("Targu-Mures"), pageNumber = 7)))

    }

    "should assemble a Sequence of HttpRequests based on a sequence of internal HttpRequestURI objects" in {
      val result = imobiliareHttpRequestProvider.addSaleTypes
        .via(imobiliareHttpRequestProvider.addPlaces)
        .via(imobiliareHttpRequestProvider.assembleHttpRequests)
        .runWith(pageNumbers, Sink.seq)
        ._2
        .futureValue
        .flatten

      assert(result.nonEmpty)
      assert(result.size == 24 * 40)

    }
  }
}
