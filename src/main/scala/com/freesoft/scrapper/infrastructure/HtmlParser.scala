package com.freesoft.scrapper.infrastructure

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

case class DomainObject(name: String)

class HtmlParser {

  //    val objects: Flow[Seq[HttpResponse], Seq[DomainObject], NotUsed] = Flow[Seq[HttpResponse]]
  //      .map(sequence =>
  //        sequence.map(
  //          httpResponse => httpResponse.entity.dataBytes.via(byteStringToString)
  //        )
  //      )

  val byteStringToString: Flow[ByteString, String, NotUsed] = Flow[ByteString]
    .map(byteString => byteString.utf8String)

  val stringToHtmlDocument: (String) => Document = { content => Jsoup.parse(content) }

  val documentSelector: (Document) => (String) => Elements = {
    document => {
      selector =>
        document.select(selector)
    }
  }

  val elementsSelector: (Elements) => (String) => Elements = {
    elements => {
      selector =>
        elements.select(selector)
    }
  }
}
