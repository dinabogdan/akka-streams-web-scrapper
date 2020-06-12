package com.freesoft.scrapper

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source


case class ImmobileType(val name: String)

class ImmobileTypeFilter(val immobileTypesFile: String)
                        (implicit materializer: Materializer) {

  require(this.getClass.getResource(s"/$immobileTypesFile") != null)

  val immobileTypes: Source[ImmobileType, NotUsed] = Source.fromIterator(() => scala.io.Source.fromResource(immobileTypesFile)
    .getLines()
    .map(name => ImmobileType(name))
  )

}
