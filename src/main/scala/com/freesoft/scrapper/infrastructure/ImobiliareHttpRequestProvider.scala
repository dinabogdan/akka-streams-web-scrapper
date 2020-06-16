package com.freesoft.scrapper.infrastructure

import akka.NotUsed
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Flow

case class HttpRequestURI(val saleType: Option[String] = Option.empty,
                          val immobileType: Option[String] = Option.empty,
                          val place: Option[String] = Option.empty,
                          val pageNumber: Int = 0) {
  def addPlace(placeFilter: PlacesFilter): HttpRequestURI = {
    copy(place = Some(placeFilter.placeName))
  }

  def addImmobileType(immobileTypesFilter: ImmobileTypesFilter): HttpRequestURI = {
    copy(immobileType = Some(immobileTypesFilter.immobileType))
  }

  def addSaleType(saleTypeFilter: SaleTypeFilter): HttpRequestURI = {
    copy(saleType = Some(saleTypeFilter.saleType))
  }
}

class ImobiliareHttpRequestProvider(
                                     val saleTypeFilterProvider: FileContentSourceProvider[SaleTypeFilter],
                                     val immobileTypesFilterProvider: FileContentSourceProvider[ImmobileTypesFilter],
                                     val placesFilterProvider: FileContentSourceProvider[PlacesFilter]
                                   ) {

  val addSaleTypes: Flow[HttpRequestURI, Seq[HttpRequestURI], NotUsed] = Flow[HttpRequestURI]
    .flatMapConcat(httpRequest => saleTypeFilterProvider.items.fold(List[HttpRequestURI]()) {
      (acc, element) => httpRequest.addSaleType(element) :: acc
    })


  val addPlaces: Flow[Seq[HttpRequestURI], Seq[HttpRequestURI], NotUsed] = Flow[Seq[HttpRequestURI]]
    .flatMapConcat(sequence =>
      placesFilterProvider.items.fold(List[HttpRequestURI]()) {
        (acc, element) =>
          acc.appendedAll(
            sequence.map(
              httpRequestURI => httpRequestURI.addPlace(element)
            )
          )
      }
    )

  val assembleHttpRequests: Flow[Seq[HttpRequestURI], Seq[HttpRequest], NotUsed] = Flow[Seq[HttpRequestURI]]
    .map(sequence =>
      sequence.map(httpRequestURI =>
        HttpRequest(uri =
          s"www.imobiliare.ro/${httpRequestURI.saleType.getOrElse()}/${httpRequestURI.place.getOrElse()}")
      )
    )
}
