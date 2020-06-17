package com.freesoft.scrapper

import akka.NotUsed
import akka.actor.{ActorRefFactory, ActorSystem}
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import com.freesoft.scrapper.infrastructure.{FileContentSourceProvider, HttpRequestURI, EstateTypesFilter, PlacesFilter, SaleTypeFilter, WebScraper, WebScraperConfig}
import org.scalatest.{BeforeAndAfterAll, Suite}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait AkkaSpec extends ScalaFutures with BeforeAndAfterAll {
  this: Suite =>

  implicit protected val system = ActorSystem()
  implicit protected val actorRefFactory: ActorRefFactory = system
  implicit protected val materializer = Materializer(system)
  implicit protected val executionContext: ExecutionContext = system.dispatcher

  override implicit def patienceConfig: PatienceConfig = super.patienceConfig.copy(timeout = 5.seconds)

  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }

  val webScraperConfig = new WebScraperConfig("application.conf")

  val saleTypeFilterProvider = new FileContentSourceProvider[SaleTypeFilter](
    "saleTypes.txt",
    { saleType => SaleTypeFilter(saleType) }
  )

  val immobileTypesFilterProvider = new FileContentSourceProvider[EstateTypesFilter](
    "estateTypes.txt",
    { immobileType => EstateTypesFilter(immobileType) }
  )

  val placesFilterProvider = new FileContentSourceProvider[PlacesFilter](
    "places.txt",
    { place => PlacesFilter(place) }
  )

  val pageNumbers: Source[HttpRequestURI, NotUsed] = Source.fromIterator(() =>
    Iterator.range(0, 10).map(i => HttpRequestURI(pageNumber = i))
  )

  val webScraper = new WebScraper(webScraperConfig)
}
