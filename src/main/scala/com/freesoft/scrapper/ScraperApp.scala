package com.freesoft.scrapper

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Source
import com.freesoft.scrapper.infrastructure._

import scala.concurrent.duration.FiniteDuration

object ScraperApp extends App with ScraperContext {

  val pageNumbers: Source[HttpRequestURI, NotUsed] = Source.fromIterator(() =>
    Iterator.range(0, 10).map(i => HttpRequestURI(pageNumber = i))
  )

  val saleTypesFilterProvider = new FileContentSourceProvider[SaleTypeFilter](
    "saleTypes.txt",
    { saleType => SaleTypeFilter(saleType) }
  )

  val immobileTypesFilterProvider = new FileContentSourceProvider[ImmobileTypesFilter](
    "immobileTypes.txt",
    { immobileType => ImmobileTypesFilter(immobileType) }
  )

  val placesFilterProvider = new FileContentSourceProvider[PlacesFilter](
    "places.txt",
    { placesType => PlacesFilter(placesType) }
  )

  val imobiliareHttpRequestProvider = new ImobiliareHttpRequestProvider(saleTypesFilterProvider, immobileTypesFilterProvider, placesFilterProvider)

  val webScraper = new WebScraper(new WebScraperConfig("application.conf"))

      pageNumbers.via(imobiliareHttpRequestProvider.addSaleTypes)
        .via(imobiliareHttpRequestProvider.addPlaces)
        .via(imobiliareHttpRequestProvider.assembleHttpRequests)
        .via(webScraper.queueRequests)
        .run()


}
