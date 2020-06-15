package com.freesoft.scrapper

import akka.NotUsed
import akka.actor.{ActorRefFactory, ActorSystem}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import com.freesoft.scrapper.infrastructure.{FileContentSourceProvider, HttpRequestURI, ImmobileTypesFilter, ImobiliareHttpRequestProvider, PlacesFilter, SaleTypeFilter}

import scala.concurrent.ExecutionContext

object ScraperApp {

  implicit val system = ActorSystem("freesoft")
  implicit protected val actorRefFactory: ActorRefFactory = system
  implicit protected val materializer = Materializer(system)
  implicit protected val executionContext: ExecutionContext = system.dispatcher

  implicit val loggingAdapter = system.log

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

  def run(): Unit = {

    pageNumbers.via(imobiliareHttpRequestProvider.addSaleTypes)


    pageNumbers.zip(
      saleTypesFilterProvider.items
    ).map {
      case (httpRequestURI: HttpRequestURI, saleTypeFilter: SaleTypeFilter) => httpRequestURI.addSaleType(saleTypeFilter)
    }.zip(
      placesFilterProvider.items
    ).map {
      case (httpRequestURI: HttpRequestURI, placeFilter: PlacesFilter) => httpRequestURI.addPlace(placeFilter)
    }

  }
}
