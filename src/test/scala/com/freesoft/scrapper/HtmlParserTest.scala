package com.freesoft.scrapper

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCode}
import akka.stream.scaladsl.Sink
import com.freesoft.scrapper.infrastructure.HtmlParser
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.scalatest.freespec.AnyFreeSpec

class HtmlParserTest extends AnyFreeSpec with AkkaSpec {

  "HtmlParser" - {
    val htmlParser = new HtmlParser
    val content = scala.io.Source.fromResource("vanzare-apartamente-bucuresti.txt")
      .getLines()
      .mkString("\n")
    val httpResponse = HttpResponse(
      entity = HttpEntity(content),
      status = StatusCode.int2StatusCode(200)
    )
    "should convert an HTML String object to a Jsoup Document" in {

      val document = htmlParser.stringToHtmlDocument(content)

      assert(document != null)
      assert(document.isInstanceOf[Document])
    }
    "should convert a ByteString to a String object which contains the entire HTML content" in {

      val content = htmlParser.byteStringToString
        .runWith(httpResponse.entity.dataBytes, Sink.seq)
        ._2
        .futureValue
        .head

      assert(content != null)

    }

    "should compose byteStringToString with stringToHtmlDocument functions and return a HTML Document" in {
      val content = htmlParser.byteStringToString
        .runWith(httpResponse.entity.dataBytes, Sink.seq)
        ._2
        .futureValue
        .head

      val document = htmlParser.stringToHtmlDocument(content)

      assert(document != null)
      assert(document.select(".container-box-anunturi") != null)
    }

    "should return the desired elements when applying selectors on a document" in {
      val content = htmlParser.byteStringToString
        .runWith(httpResponse.entity.dataBytes, Sink.seq)
        ._2
        .futureValue
        .head

      val containerBox = (htmlParser.stringToHtmlDocument andThen htmlParser.documentSelector) (content)(".container-box-anunturi")

      assert(containerBox != null)
      assert(containerBox.isInstanceOf[Elements])
    }
    "should return the desired elements when applying selectors on other elements object" in {
      val content = htmlParser.byteStringToString
        .runWith(httpResponse.entity.dataBytes, Sink.seq)
        ._2
        .futureValue
        .head

      val elements: String => Elements = (htmlParser.stringToHtmlDocument andThen htmlParser.documentSelector) (content)
      val containerBox: String => Elements = (elements andThen htmlParser.elementsSelector) (".container-box-anunturi")
      val itemScopeElements: String => Elements = (containerBox andThen htmlParser.elementsSelector) ("[itemscope]")
      val items = itemScopeElements(".box-anunt")

      assert(items != null)
      assert(items.isInstanceOf[Elements])
    }
  }
}
