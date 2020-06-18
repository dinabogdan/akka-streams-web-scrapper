package com.freesoft.scrapper.infrastructure

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import com.freesoft.scrapper.domain.Estate
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.jdk.CollectionConverters._

object stringToHtmlDocument extends (Selector => Document) {
  override def apply(content: String): Document = Jsoup.parse(content)
}

object documentSelector extends (Document => Selector => Elements) {
  override def apply(document: Document): String => Elements = {
    selector => {
      document.select(selector)
    }
  }
}

object elementsSelector extends (Elements => Selector => Elements) {
  override def apply(elements: Elements): String => Elements = {
    selector => {
      elements.select(selector)
    }
  }
}

object elementSelector extends (Element => Selector => Elements) {
  override def apply(element: Element): Selector => Elements = {
    selector => {
      element.select(selector)
    }
  }
}

object elementsAttrSelector extends (Elements => Selector => AttributeValue) {
  override def apply(element: Elements): Selector => String = {
    selector => {
      element.attr(selector)
    }
  }
}

object elementAttrSelector extends (Element => Selector => AttributeValue) {
  override def apply(element: Element): String => String = {
    selector => {
      element.attr(selector)
    }
  }
}

class HtmlParser(val transformersDispatcher: ElementTransformerDispatcher)(implicit materializer: Materializer) {

  val parseResponses: Flow[Seq[HttpResponse], Seq[Estate], NotUsed] =
    entityDataBytes.via(stringContent)
      .via(htmlDocuments)
      .via(htmlElements)
      .via(bufferedElements)
      .via(estates)

  lazy val entityDataBytes: Flow[Seq[HttpResponse], Seq[ByteString], NotUsed] = Flow[Seq[HttpResponse]]
    .map(sequence =>
      sequence.flatMap(
        httpResponse =>
          httpResponse.entity.dataBytes
            .to(Sink.seq[ByteString])
            .run()
            .asInstanceOf[Seq[ByteString]]
      )
    )

  lazy val stringContent: Flow[Seq[ByteString], Seq[String], NotUsed] = Flow[Seq[ByteString]]
    .map(sequence =>
      sequence.map(
        byteString => byteString.utf8String
      )
    )

  lazy val htmlDocuments: Flow[Seq[String], Seq[Document], NotUsed] = Flow[Seq[String]]
    .map(sequence =>
      sequence.map(
        content => stringToHtmlDocument(content)
      )
    )

  lazy val htmlElements: Flow[Seq[Document], Seq[Elements], NotUsed] = Flow[Seq[Document]]
    .map(sequence =>
      sequence.map(document => documentSelector(document)(".container-box-anunturi"))
        .map(elements => elementsSelector(elements)("[item-scope]"))
        .map(elements => elementsSelector(elements)(".box-anunt"))
    )

  lazy val bufferedElements: Flow[Seq[Elements], Seq[Element], NotUsed] = Flow[Seq[Elements]]
    .map(sequence =>
      sequence.flatMap(elements => elements.asScala)
    )

  lazy val estates: Flow[Seq[Element], Seq[Estate], NotUsed] = Flow[Seq[Element]]
    .map(sequence =>
      sequence.map(element =>
        transformersDispatcher.receiveAndTransform(element)
      )
    )


  //  val parseResponses: Flow[Seq[HttpResponse], Seq[Estate], NotUsed] = Flow[Seq[HttpResponse]]
  //    .map(sequence =>
  //      sequence.map(
  //        httpResponse =>
  //          httpResponse.entity.dataBytes
  //            .via(byteStringToString)
  //            .via(Flow[String].map(content => stringToHtmlDocument(content)))
  //            .via(Flow[Document].map(document => documentSelector(document)(".container-box-anunturi")))
  //            .via(Flow[Elements].map(elements => elementsSelector(elements)("[item-scope]")))
  //            .via(Flow[Elements].map(elements => elementsSelector(elements)(".box-anunt")))
  //            .via(Flow[Elements].map(elements => elements.asScala))
  //            .via(Flow[mutable.Buffer[Element]].map(buffer => buffer.map(element => transformersDispatcher.receiveAndTransform(element))))
  //      )
  //    )
  //  )


  val byteStringToString: Flow[ByteString, String, NotUsed] = Flow[ByteString]
    .map(byteString => byteString.utf8String)

  def elementTransformer[T]: (Element, Element => T) => T = {
    (element, f) => f(element)
  }
}
