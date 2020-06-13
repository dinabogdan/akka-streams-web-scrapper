package com.freesoft.scrapper

import akka.actor.{ActorRefFactory, ActorSystem}
import akka.stream.Materializer

import scala.concurrent.ExecutionContext

object ScraperApp {

  implicit val system = ActorSystem("freesoft")
  implicit protected val actorRefFactory: ActorRefFactory = system
  implicit protected val materializer = Materializer(system)
  implicit protected val executionContext: ExecutionContext = system.dispatcher

  implicit val loggingAdapter = system.log


}
