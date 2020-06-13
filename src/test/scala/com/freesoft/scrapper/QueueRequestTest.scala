package com.freesoft.scrapper

import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.freesoft.scrapper.infrastructure.WebScraper
import org.scalatest.freespec.AnyFreeSpec

import scala.util.{Failure, Success}

class QueueRequestTest extends AnyFreeSpec with AkkaSpec {

  "enqueue a valid request" - {
    "should return a non empty response" in {
      val webScraper = new WebScraper(webScraperConfig)
      val response = webScraper.queueRequest(HttpRequest(uri = "/vanzare-apartament/targu-mures"))

      response onComplete {
        case Success(value) =>
          val entity = value.entity
          val res = entity.dataBytes.runWith(
            Sink.fold(List[String]()) { (list: List[String], line: ByteString) => line.utf8String :: list }
          ).futureValue
          assert(res.nonEmpty)
        case Failure(exception) => assert(false == true)
      }
    }
  }

}
