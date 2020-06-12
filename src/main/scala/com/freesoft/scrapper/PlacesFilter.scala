package com.freesoft.scrapper

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.Future

case class Place(val name: String)

class PlacesFilter(val placesFile: String)
                  (implicit materializer: Materializer) {
  require(this.getClass.getResource(s"/$placesFile") != null)

  val places: Source[Place, NotUsed] = Source.fromIterator(() => scala.io.Source.fromResource(placesFile)
    .getLines()
    .map(name => Place(name))
  )

  def count(): Future[Int] =
    places.toMat(Sink.fold(0)(
      (counter, _) => counter + 1)
    )(Keep.right).run()

}
