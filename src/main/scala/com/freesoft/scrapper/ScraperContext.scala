package com.freesoft.scrapper

import akka.actor.{ActorRefFactory, ActorSystem}
import akka.event.Logging
import akka.stream.Materializer

import scala.concurrent.ExecutionContext

trait ScraperContext {
  implicit val system = ActorSystem("freesoft")

  val log = Logging(system.eventStream, "scraper.app")

  implicit protected val actorRefFactory: ActorRefFactory = system
  implicit protected val materializer = Materializer(system)
  implicit protected val executionContext: ExecutionContext = system.dispatcher

  implicit val loggingAdapter = system.log

}
