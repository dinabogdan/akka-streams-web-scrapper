package com.freesoft.scrapper.infrastructure

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.Future

sealed class SingleItemFilter(val item: String)

case class PlacesFilter(placeName: String) extends SingleItemFilter(placeName)

case class EstateTypesFilter(immobileType: String) extends SingleItemFilter(immobileType)

case class SaleTypeFilter(saleType: String) extends SingleItemFilter(saleType)

class FileContentSourceProvider[+T <: SingleItemFilter](val fileName: String,
                                                        val f: String => T)
                                                       (implicit val materializer: Materializer) {
  require(this.getClass.getResource(s"/$fileName") != null)


  def items: Source[T, NotUsed] = Source.fromIterator(() => scala.io.Source.fromResource(fileName)
    .getLines()
    .map(item => f(item))
  )

  def count(): Future[Int] =
    items.toMat(Sink.fold(0)(
      (counter, _) => counter + 1)
    )(Keep.right).run()
}
