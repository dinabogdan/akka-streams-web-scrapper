package com.freesoft.scrapper

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import com.freesoft.scrapper.infrastructure.HtmlParser
import org.jsoup.nodes.Document
import org.scalatest.freespec.AnyFreeSpec

import scala.concurrent.duration.FiniteDuration

class HtmlParserTest extends AnyFreeSpec with AkkaSpec {

  "HtmlParser" - {
    val htmlParser = new HtmlParser
    val content = scala.io.Source.fromResource("vanzare-apartamente-bucuresti.txt")
      .getLines()
      .mkString("\n")
    "should convert an HTML String object to a Jsoup Document" in {

      val document = htmlParser.stringToHtmlDocument(content)

      assert(document != null)
      assert(document.isInstanceOf[Document])
      //      val containerBoxAnunturi = document.select(".container-box-anunturi")
      //      val items = containerBoxAnunturi.select("[itemscope]").select(".box-anunt")
    }
    "should convert a ByteString to a String object which contains the entire HTML content" in {
      val request = HttpRequest(uri = "https://www.imobiliare.ro/vanzare-apartamente/targu-mures")
      val responseFuture = webScraper.queueRequest(request)
      responseFuture
        .flatMap(_.entity.toStrict(FiniteDuration(2, TimeUnit.SECONDS)))
        .map(_.data.utf8String)
        .foreach(println)
    }
  }
}
